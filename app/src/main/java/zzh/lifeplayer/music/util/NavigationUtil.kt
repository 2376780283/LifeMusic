package zzh.lifeplayer.music.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.activities.DriveModeActivity
import zzh.lifeplayer.music.activities.LicenseActivity
import zzh.lifeplayer.music.activities.SupportDevelopmentActivity
import zzh.lifeplayer.music.activities.WhatsNewFragment
import zzh.lifeplayer.music.activities.bugreport.BugReportActivity
import zzh.lifeplayer.music.extensions.showToast
import zzh.lifeplayer.music.helper.MusicPlayerRemote.audioSessionId
import com.zmusicfx.musicfx.*
object NavigationUtil {
    fun bugReport(activity: Activity) {
        activity.startActivity(
            Intent(activity, BugReportActivity::class.java), null
        )
    }

    fun goToOpenSource(activity: Activity) {
        activity.startActivity(
            Intent(activity, LicenseActivity::class.java), null
        )
    }

    fun goToSupportDevelopment(activity: Activity) {
        activity.startActivity(
            Intent(activity, SupportDevelopmentActivity::class.java), null
        )
    }

    fun gotoDriveMode(activity: Activity) {
        activity.startActivity(
            Intent(activity, DriveModeActivity::class.java), null
        )
    }

    fun gotoWhatNews(activity: FragmentActivity) {
        val changelogBottomSheet = WhatsNewFragment()
        changelogBottomSheet.show(activity.supportFragmentManager, WhatsNewFragment.TAG)
    }

/*    fun openEqualizer(activity: Activity) {
        stockEqualizer(activity)
    }
      private fun stockEqualizer(activity: Activity) {
        val sessionId = audioSessionId
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            activity.showToast(R.string.no_audio_ID, Toast.LENGTH_LONG)
        } else {
            try {
                val effects = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId)
                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                activity.startActivityForResult(effects, 0)
            } catch (notFound: ActivityNotFoundException) {
                activity.showToast(R.string.no_equalizer)
            }
        }
    }*/

fun openEqualizer(activity: Activity) {
    val sessionId = audioSessionId
    if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
     //   activity.showToast(R.string.no_audio_ID, Toast.LENGTH_LONG)
    } else {
        try {
            Intent(activity, ActivityMusic::class.java).apply {
                putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId)
                putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
            }.also { intent ->
                activity.startActivityForResult(intent, 0)
                // 添加动画
                activity.overridePendingTransition(R.anim.retro_fragment_open_enter, 0)
            }
        } catch (e: Exception) {
            activity.showToast(R.string.no_equalizer)
        }
    }
}
}