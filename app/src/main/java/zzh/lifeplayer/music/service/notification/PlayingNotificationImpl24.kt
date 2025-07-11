package zzh.lifeplayer.music.service.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import zzh.lifeplayer.appthemehelper.util.VersionUtils
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.activities.MainActivity
import zzh.lifeplayer.music.glide.LifeGlideExtension
import zzh.lifeplayer.music.glide.LifeGlideExtension.songCoverOptions
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.service.MusicService
import zzh.lifeplayer.music.service.MusicService.Companion.ACTION_QUIT
import zzh.lifeplayer.music.service.MusicService.Companion.ACTION_REWIND
import zzh.lifeplayer.music.service.MusicService.Companion.ACTION_SKIP
import zzh.lifeplayer.music.service.MusicService.Companion.ACTION_TOGGLE_PAUSE
import zzh.lifeplayer.music.service.MusicService.Companion.TOGGLE_FAVORITE
import zzh.lifeplayer.music.util.PreferenceUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@SuppressLint("RestrictedApi")
class PlayingNotificationImpl24(
    val context: MusicService,
    mediaSessionToken: MediaSessionCompat.Token,
) : PlayingNotification(context) {

    private var currentTarget: CustomTarget<Bitmap>? = null

    init {
        val action = Intent(context, MainActivity::class.java)
        action.putExtra(MainActivity.EXPAND_PANEL, PreferenceUtil.isExpandPanel)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val clickIntent =
            PendingIntent.getActivity(
                context,
                0,
                action,
                PendingIntent.FLAG_UPDATE_CURRENT or if (VersionUtils.hasMarshmallow())
                    PendingIntent.FLAG_IMMUTABLE
                else 0
            )

        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(ACTION_QUIT)
        intent.component = serviceName
        val deleteIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (VersionUtils.hasMarshmallow())
                PendingIntent.FLAG_IMMUTABLE
            else 0)
        )
        val toggleFavorite = buildFavoriteAction(false)
        val playPauseAction = buildPlayAction(true)
        val previousAction = NotificationCompat.Action(
            R.drawable.ic_skip_previous,
            context.getString(R.string.action_previous),
            retrievePlaybackAction(ACTION_REWIND)
        )
        val nextAction = NotificationCompat.Action(
            R.drawable.ic_skip_next,
            context.getString(R.string.action_next),
            retrievePlaybackAction(ACTION_SKIP)
        )
        val dismissAction = NotificationCompat.Action(
            R.drawable.ic_close,
            context.getString(R.string.action_cancel),
            retrievePlaybackAction(ACTION_QUIT)
        )
        setSmallIcon(R.drawable.ic_notification)
        setContentIntent(clickIntent)
        setDeleteIntent(deleteIntent)
        setShowWhen(false)
        addAction(toggleFavorite)
        addAction(previousAction)
        addAction(playPauseAction)
        addAction(nextAction)
        if (VersionUtils.hasS()) {
            addAction(dismissAction)
        }

        setStyle(
            MediaStyle()
                .setMediaSession(mediaSessionToken)
                .setShowActionsInCompactView(1, 2, 3)
        )
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }

    override fun updateMetadata(song: Song, onUpdate: () -> Unit) {
        if (song == Song.emptySong) return
        setContentTitle(song.title)
        setContentText(song.artistName)
        setSubText(song.albumName)

        // Displays a temporary image while Glide loads the actual album image.
        // This allows us to update the notification immediately
        // without waiting for Glide.
        setAlbumArtImage(null)
        onUpdate()

        val bigNotificationImageSize = context.resources
            .getDimensionPixelSize(R.dimen.notification_big_image_size)
        currentTarget?.let { Glide.with(context).clear(it) }
        currentTarget = Glide.with(context)
            .asBitmap()
            .songCoverOptions(song)
            .load(LifeGlideExtension.getSongModel(song))
            //.checkIgnoreMediaStore()
            .centerCrop()
            .into(object : CustomTarget<Bitmap>(
                bigNotificationImageSize,
                bigNotificationImageSize
            ) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    setLargeIcon(resource)
                    onUpdate()
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    setAlbumArtImage(null)
                    onUpdate()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    setAlbumArtImage(null)
                    onUpdate()
                }
            })
    }

    private fun setAlbumArtImage(image: Bitmap?) {
        if (image == null) {
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.default_audio_art))
        } else {
            setLargeIcon(image)
        }
    }

    private fun buildPlayAction(isPlaying: Boolean): NotificationCompat.Action {
        val playButtonResId =
            if (isPlaying) R.drawable.ic_pause_white_48dp else R.drawable.ic_play_arrow_white_48dp
        return NotificationCompat.Action.Builder(
            playButtonResId,
            context.getString(R.string.action_play_pause),
            retrievePlaybackAction(ACTION_TOGGLE_PAUSE)
        ).build()
    }

    private fun buildFavoriteAction(isFavorite: Boolean): NotificationCompat.Action {
        val favoriteResId =
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        return NotificationCompat.Action.Builder(
            favoriteResId,
            context.getString(R.string.action_toggle_favorite),
            retrievePlaybackAction(TOGGLE_FAVORITE)
        ).build()
    }

    override fun setPlaying(isPlaying: Boolean) {
        mActions[2] = buildPlayAction(isPlaying)
    }

    override fun updateFavorite(isFavorite: Boolean) {
        mActions[0] = buildFavoriteAction(isFavorite)
    }

    override fun clear(context: Context) {
        Glide.with(context).clear(currentTarget)
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or
                    if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE
                    else 0
        )
    }

    companion object {

        fun from(
            context: MusicService,
            notificationManager: NotificationManager,
            mediaSession: MediaSessionCompat,
        ): PlayingNotification {
            if (VersionUtils.hasOreo()) {
                createNotificationChannel(context, notificationManager)
            }
            return PlayingNotificationImpl24(context, mediaSession.sessionToken)
        }
    }
}