/*
 * Copyright (c) 2020 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package zzh.lifeplayer.music.fragments.folder

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.os.BundleCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zzh.lifeplayer.appthemehelper.ThemeStore.Companion.accentColor
import zzh.lifeplayer.appthemehelper.common.ATHToolbarActivity
import zzh.lifeplayer.appthemehelper.util.ToolbarContentTintHelper
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.adapter.SongFileAdapter
import zzh.lifeplayer.music.adapter.Storage
import zzh.lifeplayer.music.adapter.StorageAdapter
import zzh.lifeplayer.music.adapter.StorageClickListener
import zzh.lifeplayer.music.databinding.FragmentFolderBinding
import zzh.lifeplayer.music.extensions.dip

import zzh.lifeplayer.music.extensions.showToast
import zzh.lifeplayer.music.extensions.textColorPrimary
import zzh.lifeplayer.music.extensions.textColorSecondary
import zzh.lifeplayer.music.fragments.base.AbsMainActivityFragment
import zzh.lifeplayer.music.helper.MusicPlayerRemote.openQueueKeepShuffleMode
import zzh.lifeplayer.music.helper.ScanMusicBottomSheet

import zzh.lifeplayer.music.helper.menu.SongMenuHelper
import zzh.lifeplayer.music.helper.menu.SongsMenuHelper
import zzh.lifeplayer.music.interfaces.ICallbacks
import zzh.lifeplayer.music.interfaces.IMainActivityFragmentCallbacks
import zzh.lifeplayer.music.interfaces.IScrollHelper
import zzh.lifeplayer.music.misc.FolderMediaScannerCompletionListener

import zzh.lifeplayer.music.misc.WrappedAsyncTaskLoader
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.providers.BlacklistStore
import zzh.lifeplayer.music.util.FileUtil
import zzh.lifeplayer.music.util.PreferenceUtil.startDirectory
import zzh.lifeplayer.music.util.ThemedFastScroller.create
import zzh.lifeplayer.music.util.getExternalStorageDirectory
import zzh.lifeplayer.music.util.getExternalStoragePublicDirectory
import zzh.lifeplayer.music.views.BreadCrumbLayout
import zzh.lifeplayer.music.views.BreadCrumbLayout.Crumb
import zzh.lifeplayer.music.views.BreadCrumbLayout.SelectionCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.getValue
import kotlin.text.get

class FoldersFragment : AbsMainActivityFragment(R.layout.fragment_folder),
    IMainActivityFragmentCallbacks, SelectionCallback, ICallbacks,
    LoaderManager.LoaderCallbacks<List<File>>, StorageClickListener, IScrollHelper,
    ScanMusicBottomSheet.ScanMusicStartListener {
    private var _binding: FragmentFolderBinding? = null
    private val binding get() = _binding!!

    val toolbar: Toolbar get() = binding.appBarLayout.toolbar

    private var adapter: SongFileAdapter? = null
    private var storageAdapter: StorageAdapter? = null
    private val fileComparator = Comparator { lhs: File, rhs: File ->
        if (lhs.isDirectory && !rhs.isDirectory) {
            return@Comparator -1
        } else if (!lhs.isDirectory && rhs.isDirectory) {
            return@Comparator 1
        } else {
            return@Comparator lhs.name.compareTo(rhs.name, ignoreCase = true)
        }
    }
    private var storageItems = ArrayList<Storage>()
    private val scanViewModel: ScanViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFolderBinding.bind(view)
        mainActivity.addMusicServiceEventListener(libraryViewModel)
        mainActivity.setSupportActionBar(toolbar)
        mainActivity.supportActionBar?.title = null
        enterTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()

        setUpBreadCrumbs()
        checkForMargins()
        setUpRecyclerView()
        setUpAdapter()
        setUpTitle()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!handleBackPress()) {
                        remove()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
        if (savedInstanceState == null) {
            switchToFileAdapter()
            setCrumb(
                Crumb(
                    FileUtil.safeGetCanonicalFile(startDirectory)
                ),
                true
            )
        } else {
            binding.breadCrumbs.restoreFromStateWrapper(
                BundleCompat.getParcelable(
                    savedInstanceState,
                    CRUMBS,
                    BreadCrumbLayout.SavedStateWrapper::class.java
                )
            )
            LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
        }

    }

    private fun showScanDialogSheet(selectedFile: File)
    {

        if (selectedFile.exists()) {
            val scanBottomSheet = ScanMusicBottomSheet.newInstance(selectedFile)

            scanBottomSheet.listener = this

            scanBottomSheet.show(childFragmentManager, "ScanMusicBottomSheetTag")
        } else {
            return
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (_binding != null) {
            outState.putParcelable(CRUMBS, binding.breadCrumbs.stateWrapper)
        }
    }

    private fun setUpTitle() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_search, null, navOptions)
        }
        binding.appBarLayout.title = resources.getString(R.string.folders)
    }

    override fun onPause() {
        super.onPause()
        saveScrollPosition()
        adapter?.actionMode?.finish()
    }

    override fun handleBackPress(): Boolean {
        if (binding.breadCrumbs.popHistory()) {
            setCrumb(binding.breadCrumbs.lastHistory(), false)
            return true
        }
        return false
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<File>> {
        return AsyncFileLoader(this)
    }

    override fun onCrumbSelection(crumb: Crumb, index: Int) {
        setCrumb(crumb, true)
    }

    override fun onFileMenuClicked(file: File, view: View) {
        val popupMenu = PopupMenu(requireActivity(), view)
        if (file.isDirectory) {
            popupMenu.inflate(R.menu.menu_item_directory)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (val itemId = item.itemId) {
                    R.id.action_play_next, R.id.action_add_to_current_playing, R.id.action_add_to_playlist, R.id.action_delete_from_device -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            listSongs(
                                requireContext(),
                                listOf(file),
                                AUDIO_FILE_FILTER,
                                fileComparator
                            ) { songs ->
                                if (songs.isNotEmpty()) {
                                    SongsMenuHelper.handleMenuClick(
                                        requireActivity(), songs, itemId
                                    )
                                }
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }

                    R.id.action_add_to_blacklist -> {
                        BlacklistStore.getInstance(requireContext()).addPath(file)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.action_set_as_start_directory -> {
                        startDirectory = file
                        showToast(
                            String.format(getString(R.string.new_start_directory), file.path)
                        )
                        return@setOnMenuItemClickListener true
                    }

                    R.id.action_scan -> {
                        showScanDialogSheet(file)
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
        } else {
            popupMenu.inflate(R.menu.menu_item_file)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (val itemId = item.itemId) {
                    R.id.action_play_next, R.id.action_add_to_current_playing, R.id.action_add_to_playlist, R.id.action_go_to_album, R.id.action_go_to_artist, R.id.action_share, R.id.action_tag_editor, R.id.action_details, R.id.action_set_as_ringtone, R.id.action_delete_from_device -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            listSongs(
                                requireContext(),
                                listOf(file),
                                AUDIO_FILE_FILTER,
                                fileComparator
                            ) { songs ->
                                if (songs.isNotEmpty()) {
                                    val song = songs.first()
                                    SongMenuHelper.handleMenuClick(
                                        requireActivity(), song, itemId
                                    )
                                }
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }

                    R.id.action_scan -> {
                        showScanDialogSheet(file)
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
        }
        popupMenu.show()
    }

    override fun onFileSelected(file: File) {
        var mFile = file
        mFile = tryGetCanonicalFile(mFile)
        if (mFile.isDirectory) {
            setCrumb(Crumb(mFile), true)
        } else {
            val fileFilter = FileFilter { pathname: File ->
                !pathname.isDirectory && AUDIO_FILE_FILTER.accept(pathname)
            }
            lifecycleScope.launch(Dispatchers.IO) {
                listSongs(
                    requireContext(),
                    listOf(mFile.parentFile),
                    fileFilter,
                    fileComparator
                ) { songs ->
                    if (songs.isNotEmpty()) {
                        var startIndex = -1
                        for (i in songs.indices) {
                            if (mFile.path
                                == songs[i].data
                            ) { // path is already canonical here
                                startIndex = i
                                break
                            }
                        }
                        if (startIndex > -1) {
                            openQueueKeepShuffleMode(songs, startIndex, true)
                        } else {
                            Snackbar.make(
                                mainActivity.slidingPanel,
                                String.format(
                                    getString(R.string.not_listed_in_media_store), mFile.name

                                ).parseAsHtml(),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(
                                    R.string.action_scan
                                ) {
                                    showScanDialogSheet(mFile)

                                }
                                .setActionTextColor(accentColor(requireActivity()))
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onLoadFinished(loader: Loader<List<File>>, data: List<File>) {
        updateAdapter(data)
    }

    override fun onLoaderReset(loader: Loader<List<File>>) {
        updateAdapter(LinkedList())
    }

    override fun onMultipleItemAction(item: MenuItem, files: ArrayList<File>) {
        val itemId = item.itemId

        lifecycleScope.launch(Dispatchers.IO) {
            listSongs(requireContext(), files, AUDIO_FILE_FILTER, fileComparator) { songs ->
                if (songs.isNotEmpty()) {
                    SongsMenuHelper.handleMenuClick(
                        requireActivity(), songs, itemId
                    )
                }
            }
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(requireActivity(), toolbar)
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        menu.add(0, R.id.action_scan, 0, R.string.scan_media)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.add(0, R.id.action_go_to_start_directory, 1, R.string.action_go_to_start_directory)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.add(0, R.id.action_settings, 2, R.string.action_settings)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.removeItem(R.id.action_grid_size)
        menu.removeItem(R.id.action_layout_type)
        menu.removeItem(R.id.action_sort_order)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(
                toolbar
            )
        )
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_go_to_start_directory -> {
                setCrumb(
                    Crumb(
                        tryGetCanonicalFile(startDirectory)
                    ),
                    true
                )
                return true
            }

            R.id.action_scan -> {
                val crumb = activeCrumb
                if (crumb != null) {
                    showScanDialogSheet(crumb.file)

                }
                return true
            }

            R.id.action_settings -> {
                findNavController().navigate(
                    R.id.settings_fragment,
                    null,
                    navOptions
                )
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        checkForMargins()
    }

    private fun checkForMargins() {
        if (mainActivity.isBottomNavVisible) {
            binding.recyclerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = dip(R.dimen.bottom_nav_height)
            }
        }
    }

    private fun checkIsEmpty() {
        if (_binding != null) {
            binding.emptyEmoji.text = getEmojiByUnicode(0x1F631)
            binding.empty.isVisible = adapter?.itemCount == 0
        }
    }

    private val activeCrumb: Crumb?
        get() = if (_binding != null) {
            if (binding.breadCrumbs.size() > 0) binding.breadCrumbs.getCrumb(binding.breadCrumbs.activeIndex) else null
        } else null

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    private fun saveScrollPosition() {
        activeCrumb?.scrollPosition =
            (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    private fun scanPaths(toBeScanned: Array<String?>) {

        if (activity == null) {

            return
        }

        if (toBeScanned.isEmpty()) {

            return
        }

         lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                MediaScannerConnection.scanFile(
                    requireContext().applicationContext,
                    toBeScanned,
                    null,
                    FolderMediaScannerCompletionListener(
                        requireContext().applicationContext,
                        listOf(*toBeScanned),
                        scanViewModel
                    )
                )
            }
        }
    }

    private fun setCrumb(crumb: Crumb?, addToHistory: Boolean) {
        if (crumb == null) {
            return
        }
        val path = crumb.file.path
        if (path == "/" || path == "/storage" || path == "/storage/emulated") {
            switchToStorageAdapter()
        } else {
            saveScrollPosition()
            binding.breadCrumbs.setActiveOrAdd(crumb, false)
            if (addToHistory) {
                binding.breadCrumbs.addHistory(crumb)
            }
            LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this)
        }
    }

    private fun setUpAdapter() {
        switchToFileAdapter()
    }

    private fun setUpBreadCrumbs() {
        binding.breadCrumbs.setActivatedContentColor(
            textColorPrimary()
        )
        binding.breadCrumbs.setDeactivatedContentColor(
            textColorSecondary()

        )
        binding.breadCrumbs.setCallback(this)
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        create(
            binding.recyclerView
        )
    }

    private fun updateAdapter(files: List<File>) {
        adapter?.swapDataSet(files)
        val crumb = activeCrumb
        if (crumb != null) {
            (binding.recyclerView.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(crumb.scrollPosition, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun listPaths(
        file: File,
        fileFilter: FileFilter
    ): Array<String?> {
        return withContext(Dispatchers.IO) {
            try {
                val pathsListed: Array<String?>
                if (file.isDirectory) {
                    val files = FileUtil.listFilesDeep(file, fileFilter)
                    pathsListed = arrayOfNulls(files.size)
                    for (i in files.indices) {
                        val f = files[i]
                        pathsListed[i] = FileUtil.safeGetCanonicalPath(f)
                    }
                } else {
                    pathsListed = arrayOfNulls(1)
                    pathsListed[0] = file.path
                }
                pathsListed
            } catch (e: Exception) {
                arrayOf()
            }
        }
    }

    private class AsyncFileLoader(foldersFragment: FoldersFragment) :
        WrappedAsyncTaskLoader<List<File>>(foldersFragment.requireActivity()) {
        private val fragmentWeakReference: WeakReference<FoldersFragment> =
            WeakReference(foldersFragment)

        override fun loadInBackground(): List<File> {
            val foldersFragment = fragmentWeakReference.get()
            var directory: File? = null
            if (foldersFragment != null) {
                val crumb = foldersFragment.activeCrumb
                if (crumb != null) {
                    directory = crumb.file
                }
            }
            return if (directory != null) {
                val files = FileUtil.listFiles(
                    directory,
                    AUDIO_FILE_FILTER
                )
                Collections.sort(files, foldersFragment!!.fileComparator)
                files
            } else {
                LinkedList()
            }
        }
    }

    private suspend fun listSongs(
        context: Context,
        files: List<File?>,
        fileFilter: FileFilter,
        fileComparator: Comparator<File>,
        doOnSongsListed: (songs: List<Song>) -> Unit,
    ) {
        val songs = try {
            val fileList = FileUtil.listFilesDeep(files, fileFilter)
            Collections.sort(fileList, fileComparator)
            FileUtil.matchFilesWithMediaStore(context, fileList)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
        withContext(Dispatchers.Main) {
            doOnSongsListed(songs)
        }
    }

    override fun onStorageClicked(storage: Storage) {
        switchToFileAdapter()
        setCrumb(
            Crumb(
                FileUtil.safeGetCanonicalFile(storage.file)
            ),
            true
        )
    }

    override fun scrollToTop() {
        binding.recyclerView.scrollToPosition(0)
        binding.appBarLayout.setExpanded(true, true)
    }

    private fun switchToFileAdapter() {
        adapter = SongFileAdapter(mainActivity, LinkedList(), R.layout.item_list, this)
        adapter!!.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    checkIsEmpty()
                }
            })
        binding.recyclerView.adapter = adapter
        checkIsEmpty()
    }

    private fun switchToStorageAdapter() {
        storageItems = FileUtil.listRoots()
        storageAdapter = StorageAdapter(storageItems, this)
        binding.recyclerView.adapter = storageAdapter
        binding.breadCrumbs.clearCrumbs()
    }

    override fun onMusicScanStart(fileToScan: File) {
        scanViewModel.notifyScanStarted()
        lifecycleScope.launch {
            val pathsToScan = listPaths(fileToScan,AUDIO_FILE_FILTER)
            scanPaths(pathsToScan)
          }
    }

    companion object {
        val TAG: String = FoldersFragment::class.java.simpleName
        val AUDIO_FILE_FILTER = FileFilter { file: File ->
            (!file.isHidden
                    && (file.isDirectory
                    || FileUtil.fileIsMimeType(file, "audio/*", MimeTypeMap.getSingleton())
                    || FileUtil.fileIsMimeType(
                file,
                "application/opus",
                MimeTypeMap.getSingleton()
            )
                    || FileUtil.fileIsMimeType(
                file,
                "application/ogg",
                MimeTypeMap.getSingleton()
            )))
        }
        private const val CRUMBS = "crumbs"
        private const val LOADER_ID = 5

        // root
        val defaultStartDirectory: File
            get() {
                val musicDir =
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                val startFolder = if (musicDir.exists() && musicDir.isDirectory) {
                    musicDir
                } else {
                    val externalStorage = getExternalStorageDirectory()
                    if (externalStorage.exists() && externalStorage.isDirectory) {
                        externalStorage
                    } else {
                        File("/") // root
                    }
                }
                return startFolder
            }

        private fun tryGetCanonicalFile(file: File): File {
            return try {
                file.canonicalFile
            } catch (e: IOException) {
                e.printStackTrace()
                file
            }
        }
    }
}