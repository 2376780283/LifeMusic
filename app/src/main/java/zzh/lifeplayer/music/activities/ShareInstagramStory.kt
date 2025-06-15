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
package zzh.lifeplayer.music.activities

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.view.MenuItem
import androidx.core.net.toUri
import androidx.core.os.BundleCompat
import androidx.core.view.drawToBitmap
import zzh.lifeplayer.appthemehelper.util.ColorUtil
import zzh.lifeplayer.appthemehelper.util.MaterialValueHelper
import zzh.lifeplayer.music.activities.base.AbsThemeActivity
import zzh.lifeplayer.music.databinding.ActivityShareInstagramBinding
import zzh.lifeplayer.music.extensions.accentColor
import zzh.lifeplayer.music.extensions.setStatusBarColor
import zzh.lifeplayer.music.glide.RetroGlideExtension
import zzh.lifeplayer.music.glide.RetroGlideExtension.asBitmapPalette
import zzh.lifeplayer.music.glide.RetroGlideExtension.songCoverOptions
import zzh.lifeplayer.music.glide.RetroMusicColoredTarget
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.util.Share
import zzh.lifeplayer.music.util.color.MediaNotificationProcessor
import com.bumptech.glide.Glide

/**
 * Created by hemanths on 2020-02-02.
 */

class ShareInstagramStory : AbsThemeActivity() {

    private lateinit var binding: ActivityShareInstagramBinding

    companion object {
        const val EXTRA_SONG = "extra_song"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareInstagramBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor(Color.TRANSPARENT)

        binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
        setSupportActionBar(binding.toolbar)

        val song = intent.extras?.let { BundleCompat.getParcelable(it, EXTRA_SONG, Song::class.java) }
        song?.let { songFinal ->
            Glide.with(this)
                .asBitmapPalette()
                .songCoverOptions(songFinal)
                .load(RetroGlideExtension.getSongModel(songFinal))
                .into(object : RetroMusicColoredTarget(binding.image) {
                    override fun onColorReady(colors: MediaNotificationProcessor) {
                        setColors(colors.backgroundColor)
                    }
                })

            binding.shareTitle.text = songFinal.title
            binding.shareText.text = songFinal.artistName
            binding.shareButton.setOnClickListener {
                val path: String = Media.insertImage(
                    contentResolver,
                    binding.mainContent.drawToBitmap(Bitmap.Config.ARGB_8888),
                    "Design", null
                )
                Share.shareStoryToSocial(
                    this@ShareInstagramStory,
                    path.toUri()
                )
            }
        }
        binding.shareButton.setTextColor(
            MaterialValueHelper.getPrimaryTextColor(
                this,
                ColorUtil.isColorLight(accentColor())
            )
        )
        binding.shareButton.backgroundTintList =
            ColorStateList.valueOf(accentColor())
    }

    private fun setColors(color: Int) {
        binding.mainContent.background =
            GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(color, Color.BLACK)
            )
    }
}
