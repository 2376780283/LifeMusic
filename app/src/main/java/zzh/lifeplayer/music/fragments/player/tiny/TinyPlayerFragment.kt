package zzh.lifeplayer.music.fragments.player.tiny

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.Toolbar
import androidx.core.content.getSystemService
import zzh.lifeplayer.appthemehelper.util.ToolbarContentTintHelper
import zzh.lifeplayer.appthemehelper.util.VersionUtils
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.databinding.FragmentTinyPlayerBinding
import zzh.lifeplayer.music.extensions.*
import zzh.lifeplayer.music.fragments.base.AbsPlayerFragment
import zzh.lifeplayer.music.fragments.base.goToAlbum
import zzh.lifeplayer.music.fragments.base.goToArtist
import zzh.lifeplayer.music.fragments.player.PlayerAlbumCoverFragment
import zzh.lifeplayer.music.helper.MusicPlayerRemote
import zzh.lifeplayer.music.helper.MusicProgressViewUpdateHelper
import zzh.lifeplayer.music.helper.PlayPauseButtonOnClickHandler
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.util.MusicUtil
import zzh.lifeplayer.music.util.PreferenceUtil
import zzh.lifeplayer.music.util.ViewUtil
import zzh.lifeplayer.music.util.color.MediaNotificationProcessor
import kotlin.math.abs

class TinyPlayerFragment : AbsPlayerFragment(R.layout.fragment_tiny_player),
    MusicProgressViewUpdateHelper.Callback {
    private var _binding: FragmentTinyPlayerBinding? = null
    private val binding get() = _binding!!

    private var lastColor: Int = 0
    private var toolbarColor: Int = 0
    private var isDragEnabled = false
    lateinit var animator: ObjectAnimator

    override fun playerToolbar(): Toolbar {
        return binding.playerToolbar
    }

    override fun onShow() {}

    override fun onHide() {}

    override fun toolbarIconColor(): Int {
        return toolbarColor
    }

    override val paletteColor: Int
        get() = lastColor

    override fun onColorChanged(color: MediaNotificationProcessor) {
        lastColor = color.backgroundColor
        libraryViewModel.updateColor(color.backgroundColor)
        toolbarColor = color.secondaryTextColor
        controlsFragment.setColor(color)

        binding.title.setTextColor(color.primaryTextColor)
        binding.playerSongTotalTime.setTextColor(color.primaryTextColor)
        binding.text.setTextColor(color.secondaryTextColor)
        binding.songInfo.setTextColor(color.secondaryTextColor)
        ViewUtil.setProgressDrawable(binding.progressBar, color.backgroundColor)

        Handler(Looper.myLooper()!!).post {
            ToolbarContentTintHelper.colorizeToolbar(
                binding.playerToolbar,
                color.secondaryTextColor,
                requireActivity()
            )
        }
    }


    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }

    private lateinit var controlsFragment: TinyPlaybackControlsFragment
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        binding.title.text = song.title
        binding.text.text = String.format("%s \nby - %s", song.albumName, song.artistName)

        if (PreferenceUtil.isSongInfo) {
            binding.songInfo.text = getSongInfo(song)
            binding.songInfo.show()
        } else {
            binding.songInfo.hide()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTinyPlayerBinding.bind(view)
        binding.title.isSelected = true
        binding.progressBar.setOnClickListener(PlayPauseButtonOnClickHandler())
        binding.progressBar.setOnTouchListener(ProgressHelper(requireContext()))

        setUpPlayerToolbar()
        setUpSubFragments()
        binding.title.setOnClickListener {
            goToAlbum(requireActivity())
        }
        binding.text.setOnClickListener {
            goToArtist(requireActivity())
        }
        playerToolbar().drawAboveSystemBars()
    }

    private fun setUpSubFragments() {
        controlsFragment = whichFragment(R.id.playbackControlsFragment)
        val playerAlbumCoverFragment: PlayerAlbumCoverFragment =
            whichFragment(R.id.playerAlbumCoverFragment)
        playerAlbumCoverFragment.setCallbacks(this)
    }

    private fun setUpPlayerToolbar() {
        binding.playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            setOnMenuItemClickListener(this@TinyPlayerFragment)
        }
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        binding.progressBar.max = total

        if (isDragEnabled) {
            binding.progressBar.progress = progress
        } else {
            animator = ObjectAnimator.ofInt(binding.progressBar, "progress", progress)

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(animator)

            animatorSet.duration = 1500
            animatorSet.interpolator = LinearInterpolator()
            animatorSet.start()
        }
        binding.playerSongTotalTime.text = String.format(
            "%s/%s", MusicUtil.getReadableDurationString(total.toLong()),
            MusicUtil.getReadableDurationString(progress.toLong())
        )
    }

    inner class ProgressHelper(context: Context) : View.OnTouchListener {
        private var initialY: Int = 0
        private var initialProgress = 0
        private var progress: Int = 0
        private val displayHeight = resources.displayMetrics.heightPixels
        private var gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object :
                GestureDetector.SimpleOnGestureListener() {

                override fun onLongPress(e: MotionEvent) {
                    if (abs(e.y - initialY) <= 2) {
                        vibrate()
                        isDragEnabled = true
                        binding.progressBar.parent.requestDisallowInterceptTouchEvent(true)
                        animator.pause()
                    }
                    super.onLongPress(e)
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (abs(velocityX) > abs(velocityY)) {
                        if (velocityX < 0) {
                            MusicPlayerRemote.playNextSong()
                            return true
                        } else if (velocityX > 0) {
                            MusicPlayerRemote.playPreviousSong()
                            return true
                        }
                    }
                    return false
                }
            })
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialProgress = MusicPlayerRemote.songProgressMillis
                    initialY = event.y.toInt()
                    progressViewUpdateHelper.stop()
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    progressViewUpdateHelper.start()
                    if (isDragEnabled) {
                        MusicPlayerRemote.seekTo(progress)
                        isDragEnabled = false
                        return true
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (isDragEnabled) {
                        val diffY = (initialY - event.y).toInt()
                        progress =
                            initialProgress + diffY * (binding.progressBar.max / displayHeight) // Multiplier
                        if (progress > 0 && progress < binding.progressBar.max) {
                            onUpdateProgressViews(
                                progress,
                                MusicPlayerRemote.songDurationMillis
                            )
                        }
                    }
                }
            }
            return gestureDetector.onTouchEvent(event)
        }

        @Suppress("Deprecation")
        private fun vibrate() {
            val v = requireContext().getSystemService<Vibrator>()
            if (VersionUtils.hasOreo()) {
                v?.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v?.vibrate(10)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
