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
package zzh.lifeplayer.music.fragments.player.full

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import zzh.lifeplayer.appthemehelper.util.ToolbarContentTintHelper
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.databinding.FragmentFullBinding
import zzh.lifeplayer.music.extensions.drawAboveSystemBars
import zzh.lifeplayer.music.extensions.hide
import zzh.lifeplayer.music.extensions.show
import zzh.lifeplayer.music.extensions.whichFragment
import zzh.lifeplayer.music.fragments.base.AbsPlayerFragment
import zzh.lifeplayer.music.fragments.base.goToArtist
import zzh.lifeplayer.music.fragments.player.CoverLyricsFragment
import zzh.lifeplayer.music.fragments.player.PlayerAlbumCoverFragment
import zzh.lifeplayer.music.glide.LifeGlideExtension
import zzh.lifeplayer.music.glide.LifeGlideExtension.artistImageOptions
import zzh.lifeplayer.music.helper.MusicPlayerRemote
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.util.color.MediaNotificationProcessor
import com.bumptech.glide.Glide

class FullPlayerFragment : AbsPlayerFragment(R.layout.fragment_full) {
    private var _binding: FragmentFullBinding? = null
    private val binding get() = _binding!!

    override fun playerToolbar(): Toolbar {
        return binding.playerToolbar
    }

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor
    private lateinit var controlsFragment: FullPlaybackControlsFragment

    private fun setUpPlayerToolbar() {
        binding.playerToolbar.apply {
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFullBinding.bind(view)

        setUpSubFragments()
        setUpPlayerToolbar()
        setupArtist()
        binding.nextSong.isSelected = true
        binding.playbackControlsFragment.drawAboveSystemBars()
    }

    private fun setupArtist() {
        binding.artistImage.setOnClickListener {
            goToArtist(mainActivity)
        }
    }

    private fun setUpSubFragments() {
        controlsFragment = whichFragment(R.id.playbackControlsFragment)
        val coverFragment: PlayerAlbumCoverFragment = whichFragment(R.id.playerAlbumCoverFragment)
        coverFragment.setCallbacks(this)
        coverFragment.removeSlideEffect()
    }

    override fun onShow() {
    }

    override fun onHide() {
    }

    override fun toolbarIconColor(): Int {
        return Color.WHITE
    }

    override fun onColorChanged(color: MediaNotificationProcessor) {
        lastColor = color.backgroundColor
        binding.mask.backgroundTintList = ColorStateList.valueOf(color.backgroundColor)
        controlsFragment.setColor(color)
        libraryViewModel.updateColor(color.backgroundColor)
        ToolbarContentTintHelper.colorizeToolbar(binding.playerToolbar, Color.WHITE, activity)
        binding.coverLyrics.getFragment<CoverLyricsFragment>().setColors(color)
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
        controlsFragment.onFavoriteToggled()
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateArtistImage()
        updateLabel()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateArtistImage()
        updateLabel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateArtistImage() {
        libraryViewModel.artist(MusicPlayerRemote.currentSong.artistId)
            .observe(viewLifecycleOwner) { artist ->
                if (artist.id != -1L) {
                    Glide.with(requireActivity())
                        .load(LifeGlideExtension.getArtistModel(artist))
                        .artistImageOptions(artist)
                        .into(binding.artistImage)
                }

            }
    }

    override fun onQueueChanged() {
        super.onQueueChanged()
        if (MusicPlayerRemote.playingQueue.isNotEmpty()) updateLabel()
    }

    private fun updateLabel() {
        if ((MusicPlayerRemote.playingQueue.size - 1) == (MusicPlayerRemote.position)) {
            binding.nextSongLabel.setText(R.string.last_song)
            binding.nextSong.hide()
        } else {
            val title = MusicPlayerRemote.playingQueue[MusicPlayerRemote.position + 1].title
            binding.nextSongLabel.setText(R.string.next_song)
            binding.nextSong.apply {
                text = title
                show()
            }
        }
    }
}
