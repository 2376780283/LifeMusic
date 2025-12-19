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
package zzh.lifeplayer.music.util

import java.io.File
import java.io.IOException
import zzh.lifeplayer.music.db.PlaylistWithSongs
import zzh.lifeplayer.music.helper.M3UWriter.writeIO

object PlaylistsUtil {
    @Throws(IOException::class)
    fun savePlaylistWithSongs(playlist: PlaylistWithSongs?): File {
        return writeIO(File(getExternalStorageDirectory(), "Playlists"), playlist!!)
    }
}
