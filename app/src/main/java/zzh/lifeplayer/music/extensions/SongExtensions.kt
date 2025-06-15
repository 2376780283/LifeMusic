package zzh.lifeplayer.music.extensions

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat.QueueItem
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.util.MusicUtil

val Song.uri get() = MusicUtil.getSongFileUri(songId = id)

val Song.albumArtUri get() = MusicUtil.getMediaStoreAlbumCoverUri(albumId)

fun ArrayList<Song>.toMediaSessionQueue(): List<QueueItem> {
    return map { song ->
        val mediaDescription = MediaDescriptionCompat.Builder()
            .setMediaId(song.id.toString())
            .setTitle(song.title)
            .setSubtitle(song.artistName)
            .setIconUri(song.albumArtUri)
            .build()
        QueueItem(mediaDescription, song.hashCode().toLong())
    }
}
