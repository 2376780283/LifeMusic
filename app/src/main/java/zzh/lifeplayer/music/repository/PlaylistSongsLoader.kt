/*
 * Copyright (c) 2019 Hemanth Savarala.
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

package zzh.lifeplayer.music.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.Audio.Playlists.Members
import zzh.lifeplayer.music.Constants
import zzh.lifeplayer.music.Constants.IS_MUSIC
import zzh.lifeplayer.music.extensions.getInt
import zzh.lifeplayer.music.extensions.getLong
import zzh.lifeplayer.music.extensions.getString
import zzh.lifeplayer.music.extensions.getStringOrNull
import zzh.lifeplayer.music.model.PlaylistSong
import zzh.lifeplayer.music.model.Song

/**
 * Created by hemanths on 16/08/17.
 */
@Suppress("Deprecation")
object PlaylistSongsLoader {

    @JvmStatic
    fun getPlaylistSongList(context: Context, playlistId: Long): List<Song> {
        val songs = mutableListOf<Song>()
        val cursor =
            makePlaylistSongCursor(
                context,
                playlistId
            )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(
                    getPlaylistSongFromCursorImpl(
                        cursor,
                        playlistId
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }

    // TODO duplicated in [PlaylistRepository.kt]
    private fun getPlaylistSongFromCursorImpl(cursor: Cursor, playlistId: Long): PlaylistSong {
        val id = cursor.getLong(Members.AUDIO_ID)
        val title = cursor.getString(AudioColumns.TITLE)
        val trackNumber = cursor.getInt(AudioColumns.TRACK)
        val year = cursor.getInt(AudioColumns.YEAR)
        val duration = cursor.getLong(AudioColumns.DURATION)
        val data = cursor.getString(Constants.DATA)
        val dateModified = cursor.getLong(AudioColumns.DATE_MODIFIED)
        val albumId = cursor.getLong(AudioColumns.ALBUM_ID)
        val albumName = cursor.getString(AudioColumns.ALBUM)
        val artistId = cursor.getLong(AudioColumns.ARTIST_ID)
        val artistName = cursor.getString(AudioColumns.ARTIST)
        val idInPlaylist = cursor.getLong(Members._ID)
        val composer = cursor.getStringOrNull(AudioColumns.COMPOSER)
        val albumArtist = cursor.getStringOrNull("album_artist")
        return PlaylistSong(
            id,
            title,
            trackNumber,
            year,
            duration,
            data,
            dateModified,
            albumId,
            albumName,
            artistId,
            artistName,
            playlistId,
            idInPlaylist,
            composer,
            albumArtist
        )
    }

    private fun makePlaylistSongCursor(context: Context, playlistId: Long): Cursor? {
        try {
            return context.contentResolver.query(
                Members.getContentUri("external", playlistId),
                arrayOf(
                    Members.AUDIO_ID, // 0
                    AudioColumns.TITLE, // 1
                    AudioColumns.TRACK, // 2
                    AudioColumns.YEAR, // 3
                    AudioColumns.DURATION, // 4
                    Constants.DATA, // 5
                    AudioColumns.DATE_MODIFIED, // 6
                    AudioColumns.ALBUM_ID, // 7
                    AudioColumns.ALBUM, // 8
                    AudioColumns.ARTIST_ID, // 9
                    AudioColumns.ARTIST, // 10
                    Members._ID,//11
                    AudioColumns.COMPOSER,//12
                    "album_artist"//13
                ), IS_MUSIC, null, Members.DEFAULT_SORT_ORDER
            )
        } catch (e: SecurityException) {
            return null
        }
    }
}
