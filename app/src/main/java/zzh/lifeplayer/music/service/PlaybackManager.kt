package zzh.lifeplayer.music.service

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.zmusicfx.musicfx.ControlPanelEffect
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.service.playback.Playback
import zzh.lifeplayer.music.util.PreferenceUtil

class PlaybackManager(val context: Context) {

    var playback: Playback? = null
    private var playbackLocation = PlaybackLocation.LOCAL

    val isLocalPlayback
        get() = playbackLocation == PlaybackLocation.LOCAL

    val audioSessionId: Int
        get() =
            if (playback != null) {
                playback!!.audioSessionId
            } else 0

    val songDurationMillis: Int
        get() =
            if (playback != null) {
                playback!!.duration()
            } else -1

    val songProgressMillis: Int
        get() =
            if (playback != null) {
                playback!!.position()
            } else -1

    val isPlaying: Boolean
        get() = playback != null && playback!!.isPlaying

    init {
        playback = createLocalPlayback()
    }

    fun setCallbacks(callbacks: Playback.PlaybackCallbacks) {
        playback?.callbacks = callbacks
    }

    fun play(onNotInitialized: () -> Unit) {
        if (playback != null && !playback!!.isPlaying) {
            if (!playback!!.isInitialized) {
                onNotInitialized()
            } else {
                if (playbackLocation == PlaybackLocation.LOCAL) {
                    if (playback is CrossFadePlayer) {
                        if (!(playback as CrossFadePlayer).isCrossFading) {
                            AudioFader.startFadeAnimator(playback!!, true)
                        }
                    } else {
                        AudioFader.startFadeAnimator(playback!!, true)
                    }
                }
                playback?.start()
                openAudioEffectSession()
            }
        }
    }

    fun pause(force: Boolean, onPause: () -> Unit) {
        if (playback != null && playback!!.isPlaying) {
            if (force) {
                playback?.pause()
                closeAudioEffectSession()
                onPause()
            } else {
                AudioFader.startFadeAnimator(playback!!, false) {
                    // Code to run when Animator Ends
                    playback?.pause()
                    closeAudioEffectSession()
                    onPause()
                }
            }
        }
    }

    fun seek(millis: Int, force: Boolean): Int = playback!!.seek(millis, force)

    fun setDataSource(song: Song, force: Boolean, completion: (success: Boolean) -> Unit) {
        playback?.setDataSource(song, force, completion)
    }

    fun setNextDataSource(trackUri: Uri?) {
        playback?.setNextDataSource(trackUri)
    }

    fun setCrossFadeDuration(duration: Int) {
        playback?.setCrossFadeDuration(duration)
    }

    /**
     * @param crossFadeDuration CrossFade duration
     * @return Whether switched playback
     */
    fun maybeSwitchToCrossFade(crossFadeDuration: Int): Boolean {
        /* Switch to RetroExoPlayer if CrossFade duration is 0 and
        Playback is not an instance of RetroExoPlayer */
        if (playback !is LifeExoPlayer && crossFadeDuration == 0) {
            if (playback != null) {
                playback?.release()
            }
            playback = null
            playback = LifeExoPlayer(context)
            return true
        } else if (playback !is CrossFadePlayer && crossFadeDuration > 0) {
            if (playback != null) {
                playback?.release()
            }
            playback = null
            playback = CrossFadePlayer(context)
            return true
        }
        return false
    }

    fun release() {
        playback?.release()
        playback = null
        closeAudioEffectSession()
    }

    private fun openAudioEffectSession() {
        val audioSessionId = playback!!.audioSessionId
        if (audioSessionId != 0) {
            Handler(Looper.getMainLooper())
                .postDelayed(
                    {
                        // 直接使用 ControlPanelEffect 初始化效果会话
                        ControlPanelEffect.openSession(context, context.packageName, audioSessionId)
                        ControlPanelEffect.setEnabledAll(
                            context,
                            context.packageName,
                            audioSessionId,
                            true,
                        )
                    },
                    500,
                )
        }
    }

    private fun closeAudioEffectSession() {
        val audioSessionId = playback!!.audioSessionId
        if (audioSessionId != 0) {
            ControlPanelEffect.setEnabledAll(context, context.packageName, audioSessionId, false)
            ControlPanelEffect.closeSession(context, context.packageName, audioSessionId)
        }
    }

    /**
     * Reopens the audio effect session. This should be called when the audio session ID changes
     * (e.g., when a new track starts) to ensure equalizer and other audio effects remain active.
     */
    fun reopenAudioEffectSession() {
        if (playback != null && playback!!.isPlaying) {
            closeAudioEffectSession()
            openAudioEffectSession()
        }
    }

    fun switchToLocalPlayback(onChange: (wasPlaying: Boolean, progress: Int) -> Unit) {
        playbackLocation = PlaybackLocation.LOCAL
        switchToPlayback(createLocalPlayback(), onChange)
    }

    fun switchToRemotePlayback(
        castPlayer: CastPlayer,
        onChange: (wasPlaying: Boolean, progress: Int) -> Unit,
    ) {
        playbackLocation = PlaybackLocation.REMOTE
        switchToPlayback(castPlayer, onChange)
    }

    private fun switchToPlayback(
        playback: Playback,
        onChange: (wasPlaying: Boolean, progress: Int) -> Unit,
    ) {
        val oldPlayback = this.playback
        val wasPlaying: Boolean = oldPlayback?.isPlaying == true
        val progress: Int = oldPlayback?.position() ?: 0
        this.playback = playback
        oldPlayback?.stop()
        onChange(wasPlaying, progress)
    }

    private fun createLocalPlayback(): Playback {
        // Set RetroExoPlayer when crossfade duration is 0 i.e. off
        return if (PreferenceUtil.crossFadeDuration == 0) {
            LifeExoPlayer(context)
        } else {
            CrossFadePlayer(context)
        }
    }

    fun setPlaybackSpeedPitch(playbackSpeed: Float, playbackPitch: Float) {
        playback?.setPlaybackSpeedPitch(playbackSpeed, playbackPitch)
    }
}

enum class PlaybackLocation {
    LOCAL,
    REMOTE,
}
