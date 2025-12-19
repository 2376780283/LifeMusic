package zzh.lifeplayer.music.interfaces

import android.view.View
import zzh.lifeplayer.music.db.PlaylistWithSongs

interface IPlaylistClickListener {
    fun onPlaylistClick(playlistWithSongs: PlaylistWithSongs, view: View)
}
