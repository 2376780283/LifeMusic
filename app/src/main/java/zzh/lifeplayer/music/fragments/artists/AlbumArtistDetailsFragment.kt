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
package zzh.lifeplayer.music.fragments.artists

import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AlbumArtistDetailsFragment : AbsArtistDetailsFragment() {

    private val arguments by navArgs<AlbumArtistDetailsFragmentArgs>()

    override val detailsViewModel: ArtistDetailsViewModel by viewModel {
        parametersOf(null, arguments.extraArtistName)
    }
    override val artistId: Long?
        get() = null
    override val artistName: String
        get() = arguments.extraArtistName
}
