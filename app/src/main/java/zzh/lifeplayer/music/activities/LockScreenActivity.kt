package zzh.lifeplayer.music.activities

import android.app.KeyguardManager
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.getSystemService
import zzh.lifeplayer.appthemehelper.util.VersionUtils
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.activities.base.AbsMusicServiceActivity
import zzh.lifeplayer.music.databinding.ActivityLockScreenBinding
import zzh.lifeplayer.music.extensions.hideStatusBar
import zzh.lifeplayer.music.extensions.setTaskDescriptionColorAuto
import zzh.lifeplayer.music.extensions.whichFragment
import zzh.lifeplayer.music.fragments.player.lockscreen.LockScreenControlsFragment
import zzh.lifeplayer.music.glide.LifeGlideExtension
import zzh.lifeplayer.music.glide.LifeGlideExtension.asBitmapPalette
import zzh.lifeplayer.music.glide.LifeGlideExtension.songCoverOptions
import zzh.lifeplayer.music.glide.LifeMusicColoredTarget
import zzh.lifeplayer.music.helper.MusicPlayerRemote
import zzh.lifeplayer.music.util.color.MediaNotificationProcessor
import com.bumptech.glide.Glide
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition

class LockScreenActivity : AbsMusicServiceActivity() {
    private lateinit var binding: ActivityLockScreenBinding
    private var fragment: LockScreenControlsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lockScreenInit()
        binding = ActivityLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()
        setTaskDescriptionColorAuto()

        val config = SlidrConfig.Builder().listener(object : SlidrListener {
            override fun onSlideStateChanged(state: Int) {
            }

            override fun onSlideChange(percent: Float) {
            }

            override fun onSlideOpened() {
            }

            override fun onSlideClosed(): Boolean {
                if (VersionUtils.hasOreo()) {
                    val keyguardManager =
                        getSystemService<KeyguardManager>()
                    keyguardManager?.requestDismissKeyguard(this@LockScreenActivity, null)
                }
                finish()
                return true
            }
        }).position(SlidrPosition.BOTTOM).build()

        Slidr.attach(this, config)

        fragment = whichFragment<LockScreenControlsFragment>(R.id.playback_controls_fragment)

        binding.slide.apply {
            translationY = 100f
            alpha = 0f
            animate().translationY(0f).alpha(1f).setDuration(1500).start()
        }
    }

    @Suppress("Deprecation")
    private fun lockScreenInit() {
        if (VersionUtils.hasOreoMR1()) {
            setShowWhenLocked(true)
            //setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                //          or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSongs()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSongs()
    }

    private fun updateSongs() {
        val song = MusicPlayerRemote.currentSong
        Glide.with(this)
            .asBitmapPalette()
            .songCoverOptions(song)
            .load(LifeGlideExtension.getSongModel(song))
            .dontAnimate()
            .into(object : LifeMusicColoredTarget(binding.image) {
                override fun onColorReady(colors: MediaNotificationProcessor) {
                    fragment?.setColor(colors)
                }
            })
    }
}
