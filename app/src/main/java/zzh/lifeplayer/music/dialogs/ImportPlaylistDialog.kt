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
package zzh.lifeplayer.music.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.extensions.colorButtons
import zzh.lifeplayer.music.extensions.materialDialog
import zzh.lifeplayer.music.fragments.LibraryViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ImportPlaylistDialog : DialogFragment() {
    private val libraryViewModel by activityViewModel<LibraryViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return materialDialog(R.string.import_playlist)
            .setMessage(R.string.import_playlist_message)
            .setPositiveButton(R.string.import_label) { _, _ ->
                try {
                    libraryViewModel.importPlaylists()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .create()
            .colorButtons()
    }
}
