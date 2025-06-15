package zzh.lifeplayer.music.fragments.home

import android.os.Bundle
import android.view.*
import android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import zzh.lifeplayer.appthemehelper.common.ATHToolbarActivity
import zzh.lifeplayer.appthemehelper.util.ColorUtil
import zzh.lifeplayer.appthemehelper.util.ToolbarContentTintHelper
import zzh.lifeplayer.music.*
import zzh.lifeplayer.music.adapter.HomeAdapter
import zzh.lifeplayer.music.databinding.FragmentHomeBinding
import zzh.lifeplayer.music.dialogs.CreatePlaylistDialog
import zzh.lifeplayer.music.dialogs.ImportPlaylistDialog
import zzh.lifeplayer.music.extensions.accentColor
import zzh.lifeplayer.music.extensions.dip
import zzh.lifeplayer.music.extensions.elevatedAccentColor
import zzh.lifeplayer.music.extensions.setUpMediaRouteButton
import zzh.lifeplayer.music.fragments.ReloadType
import zzh.lifeplayer.music.fragments.base.AbsMainActivityFragment
import zzh.lifeplayer.music.glide.RetroGlideExtension
import zzh.lifeplayer.music.glide.RetroGlideExtension.profileBannerOptions
import zzh.lifeplayer.music.glide.RetroGlideExtension.songCoverOptions
import zzh.lifeplayer.music.glide.RetroGlideExtension.userProfileOptions

import zzh.lifeplayer.music.glide.BlurTransformation
import zzh.lifeplayer.music.glide.RetroGlideExtension.simpleSongCoverOptions
import zzh.lifeplayer.music.glide.crossfadeListener

import zzh.lifeplayer.music.helper.MusicPlayerRemote
import zzh.lifeplayer.music.interfaces.IScrollHelper
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.util.PreferenceUtil
import zzh.lifeplayer.music.util.PreferenceUtil.userName
import zzh.lifeplayer.music.util.PreferenceUtil.blurAmount

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions

import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class HomeFragment :
    AbsMainActivityFragment(R.layout.fragment_home), IScrollHelper {

    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeBinding = FragmentHomeBinding.bind(view)
        _binding = HomeBinding(homeBinding)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.title = null
        setupListeners()
        binding.titleWelcome.text = String.format("%s", userName)
        enterTransition = MaterialFadeThrough().addTarget(binding.contentContainer)
        reenterTransition = MaterialFadeThrough().addTarget(binding.contentContainer)

        checkForMargins()

        val homeAdapter = HomeAdapter(mainActivity)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(mainActivity)
            adapter = homeAdapter
        }
        libraryViewModel.getSuggestions().observe(viewLifecycleOwner) {
            loadSuggestions(it)
        }
        libraryViewModel.getHome().observe(viewLifecycleOwner) {
            homeAdapter.swapData(it)
        }

        loadProfile()
        setupTitle()
        colorButtons()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        view.doOnLayout {
            adjustPlaylistButtons()
        }
    }

    private fun adjustPlaylistButtons() {
        val buttons =
            listOf(binding.history, binding.lastAdded, binding.topPlayed, binding.actionShuffle)
        buttons.maxOf { it.lineCount }.let { maxLineCount ->
            buttons.forEach { button ->
                // Set the highest line count to every button for consistency
                button.setLines(maxLineCount)
            }
        }
    }

    private fun setupListeners() {
    /*
        binding.bannerImage?.setOnClickListener {
            findNavController().navigate(
                R.id.user_info_fragment, null, null, FragmentNavigatorExtras(
                    binding.userImage to "user_image"
                )
            )
            reenterTransition = null
        }
    */
        binding.lastAdded.setOnClickListener {
            findNavController().navigate(
                R.id.detailListFragment,
                bundleOf(EXTRA_PLAYLIST_TYPE to LAST_ADDED_PLAYLIST)
            )
            setSharedAxisYTransitions()
        }

        binding.topPlayed.setOnClickListener {
            findNavController().navigate(
                R.id.detailListFragment,
                bundleOf(EXTRA_PLAYLIST_TYPE to TOP_PLAYED_PLAYLIST)
            )
            setSharedAxisYTransitions()
        }

        binding.actionShuffle.setOnClickListener {
            libraryViewModel.shuffleSongs()
        }

        binding.history.setOnClickListener {
            findNavController().navigate(
                R.id.detailListFragment,
                bundleOf(EXTRA_PLAYLIST_TYPE to HISTORY_PLAYLIST)
            )
            setSharedAxisYTransitions()
        }

        binding.userImage.setOnClickListener {
            findNavController().navigate(
                R.id.user_info_fragment, null, null, FragmentNavigatorExtras(
                    binding.userImage to "user_image"
                )
            )
        }
        // Reload suggestions
        binding.suggestions.refreshButton.setOnClickListener {
            libraryViewModel.forceReload(
                ReloadType.Suggestions
            )
        }
    }

    private fun setupTitle() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_search, null, navOptions)
        }
 //        不再使用hex color 
 //        val hexColor = String.format("#%06X", 0xFFFFFF and accentColor())
 //        val hexColor = "#ff33b5e5" // use yin bian ma
        val appName = "Life Music"
        binding.appBarLayout.title = appName
    }
    
   private fun loadProfile() {
   /*
    binding.bannerImage?.let {
        Glide.with(this)
            .load(RetroGlideExtension.getBannerModel())
            .profileBannerOptions(RetroGlideExtension.getBannerModel())
              .transform(
                BlurTransformation.Builder(requireContext()).blurRadius(blurAmount.toFloat())
                    .build()
            )
            .into(it)
    }
   */
    binding.bannerImagelarge?.let {
        Glide.with(this)
            .load(RetroGlideExtension.getBannerModel())
            .profileBannerOptions(RetroGlideExtension.getBannerModel())
            .into(it)
    }
    

    Glide.with(requireActivity())
        .load(RetroGlideExtension.getUserModel())
        .userProfileOptions(RetroGlideExtension.getUserModel(), requireContext())
        .into(binding.userImage)
    }

    fun colorButtons() {
        binding.history.elevatedAccentColor()
        binding.lastAdded.elevatedAccentColor()
        binding.topPlayed.elevatedAccentColor()
        binding.actionShuffle.elevatedAccentColor()
    }
    

    private fun checkForMargins() {
        if (mainActivity.isBottomNavVisible) {
            binding.recyclerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = dip(R.dimen.bottom_nav_height)
            }
        }
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        menu.removeItem(R.id.action_grid_size)
        menu.removeItem(R.id.action_layout_type)
        menu.removeItem(R.id.action_sort_order)
        menu.findItem(R.id.action_settings).setShowAsAction(SHOW_AS_ACTION_IF_ROOM)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(),
            binding.toolbar,
            menu,
            ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar)
        )
        //Setting up cast button
        requireContext().setUpMediaRouteButton(menu) 
    }

    override fun scrollToTop() {
        binding.container.scrollTo(0, 0)
        binding.appBarLayout.setExpanded(true)
    }

    fun setSharedAxisXTransitions() {
        exitTransition =
            MaterialSharedAxis(MaterialSharedAxis.X, true).addTarget(CoordinatorLayout::class.java)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    private fun setSharedAxisYTransitions() {
        exitTransition =
            MaterialSharedAxis(MaterialSharedAxis.Y, true).addTarget(CoordinatorLayout::class.java)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    private fun loadSuggestions(songs: List<Song>) { // 建议song 部分
        if (!PreferenceUtil.homeSuggestions || songs.isEmpty()) {
     //  if (!PreferenceUtil.homeSuggestions) {
            binding.suggestions.root.isVisible = false
            return
        }
        val images = listOf(
            binding.suggestions.image1,
            binding.suggestions.image2,
            binding.suggestions.image3,
            binding.suggestions.image4,
            binding.suggestions.image5,
            binding.suggestions.image6,
            binding.suggestions.image7,
            binding.suggestions.image8
        )
        val color = accentColor()
        binding.suggestions.message.apply {
            setTextColor(color)
            setOnClickListener {
                it.isClickable = false
                it.postDelayed({ it.isClickable = true }, 500)
                MusicPlayerRemote.playNext(songs.subList(0, 8))
                if (!MusicPlayerRemote.isPlaying) {
                    MusicPlayerRemote.playNextSong()
                }
            }
        }
        binding.suggestions.card6.setCardBackgroundColor(ColorUtil.withAlpha(color, 0.12f))
        images.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                it.isClickable = false
                it.postDelayed({ it.isClickable = true }, 500)
                MusicPlayerRemote.playNext(songs[index])
                if (!MusicPlayerRemote.isPlaying) {
                    MusicPlayerRemote.playNextSong()
                }
            }
            Glide.with(this)
                .load(RetroGlideExtension.getSongModel(songs[index])) //出错代码部分
                .songCoverOptions(songs[index])
                .into(imageView)
        }
    }
    /*
    private fun loadSuggestions(songs: List<Song>) {
    if (!PreferenceUtil.homeSuggestions || songs.isEmpty()) {
        binding.suggestions.root.isVisible = false
        return
    }

    binding.suggestions.root.isVisible = true

    val images = listOf(
        binding.suggestions.image1,
        binding.suggestions.image2,
        binding.suggestions.image3,
        binding.suggestions.image4,
        binding.suggestions.image5,
        binding.suggestions.image6,
        binding.suggestions.image7,
        binding.suggestions.image8
    )

    val color = accentColor()
    val maxItems = minOf(songs.size, images.size)

    binding.suggestions.message.apply {
        setTextColor(color)
        setOnClickListener {
            it.isClickable = false
            it.postDelayed({ it.isClickable = true }, 500)
            MusicPlayerRemote.playNext(songs.take(maxItems))
            if (!MusicPlayerRemote.isPlaying) {
                MusicPlayerRemote.playNextSong()
            }
        }
    }

    binding.suggestions.card6.setCardBackgroundColor(ColorUtil.withAlpha(color, 0.12f))

    for (index in 0 until maxItems) {
        val song = songs[index]
        val imageView = images[index]

        imageView.isVisible = true
        imageView.setOnClickListener {
            it.isClickable = false
            it.postDelayed({ it.isClickable = true }, 500)
            MusicPlayerRemote.playNext(song)
            if (!MusicPlayerRemote.isPlaying) {
                MusicPlayerRemote.playNextSong()
            }
        }

        Glide.with(this)
            .load(RetroGlideExtension.getSongModel(song))
            .songCoverOptions(song)
            .into(imageView)
    }

    // 隐藏未使用的 imageView
    for (i in maxItems until images.size) {
        images[i].isVisible = false
    }
}*/

    companion object {

        const val TAG: String = "BannerHomeFragment"

        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> findNavController().navigate(
                R.id.settings_fragment,
                null,
                navOptions
            )

            R.id.action_import_playlist -> ImportPlaylistDialog().show(
                childFragmentManager,
                "ImportPlaylist"
            )

            R.id.action_add_to_playlist -> CreatePlaylistDialog.create(emptyList()).show(
                childFragmentManager,
                "ShowCreatePlaylistDialog"
            )
        }
        return false
    }

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(requireActivity(), binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        checkForMargins()
        libraryViewModel.forceReload(ReloadType.HomeSections)
        exitTransition = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
