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

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import zzh.lifeplayer.music.interfaces.IMusicServiceEventListener
import zzh.lifeplayer.music.model.Artist
import zzh.lifeplayer.music.network.Result
import zzh.lifeplayer.music.network.model.LastFmArtist
import zzh.lifeplayer.music.repository.RealRepository

class ArtistDetailsViewModel(
    private val realRepository: RealRepository,
    private val artistId: Long?,
    private val artistName: String?,
) : ViewModel(), IMusicServiceEventListener {
    private val artistDetails = MutableLiveData<Artist>()

    init {
        fetchArtist()
    }

    private fun fetchArtist() {
        viewModelScope.launch(IO) {
            artistId?.let { artistDetails.postValue(realRepository.artistById(it)) }

            artistName?.let { artistDetails.postValue(realRepository.albumArtistByName(it)) }
        }
    }

    fun getArtist(): LiveData<Artist> = artistDetails

    fun getArtistInfo(name: String, lang: String?, cache: String?): LiveData<Result<LastFmArtist>> =
        liveData(IO) {
            emit(Result.Loading)
            val info = realRepository.artistInfo(name, lang, cache)
            emit(info)
        }

    override fun onMediaStoreChanged() {
        fetchArtist()
    }

    override fun onServiceConnected() {}

    override fun onServiceDisconnected() {}

    override fun onQueueChanged() {}

    override fun onPlayingMetaChanged() {}

    override fun onPlayStateChanged() {}

    override fun onRepeatModeChanged() {}

    override fun onShuffleModeChanged() {}

    override fun onFavoriteStateChanged() {}
}
