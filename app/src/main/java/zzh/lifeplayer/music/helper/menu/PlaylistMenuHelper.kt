/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package zzh.lifeplayer.music.helper.menu

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.db.PlaylistWithSongs
import zzh.lifeplayer.music.db.toSongs
import zzh.lifeplayer.music.dialogs.AddToPlaylistDialog
import zzh.lifeplayer.music.dialogs.DeletePlaylistDialog
import zzh.lifeplayer.music.dialogs.RenamePlaylistDialog
import zzh.lifeplayer.music.dialogs.SavePlaylistDialog
import zzh.lifeplayer.music.helper.MusicPlayerRemote
import zzh.lifeplayer.music.repository.RealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object PlaylistMenuHelper : KoinComponent {

    fun handleMenuClick(
        activity: FragmentActivity,
        playlistWithSongs: PlaylistWithSongs,
        item: MenuItem
    ): Boolean {
        when (item.itemId) {
            R.id.action_play -> {
                MusicPlayerRemote.openQueue(playlistWithSongs.songs.toSongs(), 0, true)
                return true
            }
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(playlistWithSongs.songs.toSongs())
                return true
            }
            R.id.action_add_to_playlist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlists = get<RealRepository>().fetchPlaylists()
                    withContext(Dispatchers.Main) {
                        AddToPlaylistDialog.create(playlists, playlistWithSongs.songs.toSongs())
                            .show(activity.supportFragmentManager, "ADD_PLAYLIST")
                    }
                }
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(playlistWithSongs.songs.toSongs())
                return true
            }
            R.id.action_rename_playlist -> {
                RenamePlaylistDialog.create(playlistWithSongs.playlistEntity)
                    .show(activity.supportFragmentManager, "RENAME_PLAYLIST")
                return true
            }
            R.id.action_delete_playlist -> {
                DeletePlaylistDialog.create(playlistWithSongs.playlistEntity)
                    .show(activity.supportFragmentManager, "DELETE_PLAYLIST")
                return true
            }
            R.id.action_save_playlist -> {
                SavePlaylistDialog.create(playlistWithSongs)
                    .show(activity.supportFragmentManager, "SavePlaylist")
                return true
            }
        }
        return false
    }
}
