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
package zzh.lifeplayer.music.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zzh.lifeplayer.music.*
import zzh.lifeplayer.music.adapter.album.AlbumAdapter
import zzh.lifeplayer.music.adapter.artist.ArtistAdapter
import zzh.lifeplayer.music.adapter.song.SongAdapter
import zzh.lifeplayer.music.fragments.home.HomeFragment
import zzh.lifeplayer.music.interfaces.IAlbumClickListener
import zzh.lifeplayer.music.interfaces.IArtistClickListener
import zzh.lifeplayer.music.model.Album
import zzh.lifeplayer.music.model.Artist
import zzh.lifeplayer.music.model.Home
import zzh.lifeplayer.music.model.Song
import zzh.lifeplayer.music.util.PreferenceUtil

class HomeAdapter(private val activity: AppCompatActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), IArtistClickListener, IAlbumClickListener {

    private val EMPTY_VIEW = 999
    private var list = listOf<Home>()

    override fun getItemViewType(position: Int): Int {
        return if (list.isEmpty()) {
            EMPTY_VIEW
        } else {
            list[position].homeSection
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RECENT_ARTISTS,
            TOP_ARTISTS -> {
                val layout =
                    LayoutInflater.from(activity)
                        .inflate(R.layout.section_recycler_view, parent, false)
                ArtistViewHolder(layout)
            }
            FAVOURITES -> {
                val layout =
                    LayoutInflater.from(activity)
                        .inflate(R.layout.section_recycler_view, parent, false)
                PlaylistViewHolder(layout)
            }
            TOP_ALBUMS,
            RECENT_ALBUMS -> {
                val layout =
                    LayoutInflater.from(activity)
                        .inflate(R.layout.section_recycler_view, parent, false)
                AlbumViewHolder(layout)
            }
            EMPTY_VIEW -> {
                val layout =
                    LayoutInflater.from(activity).inflate(R.layout.empty_state_view, parent, false)
                EmptyViewHolder(layout)
            }
            else -> {
                val layout =
                    LayoutInflater.from(activity)
                        .inflate(R.layout.section_recycler_view, parent, false)
                ArtistViewHolder(layout)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AlbumViewHolder -> {
                val home = list[position]
                holder.bindView(home)
                holder.clickableArea.setOnClickListener {
                    it.findFragment<HomeFragment>().setSharedAxisXTransitions()
                    activity
                        .findNavController(R.id.fragment_container)
                        .navigate(R.id.detailListFragment, bundleOf("type" to home.homeSection))
                }
            }
            is ArtistViewHolder -> {
                val home = list[position]
                holder.bindView(home)
                holder.clickableArea.setOnClickListener {
                    it.findFragment<HomeFragment>().setSharedAxisXTransitions()
                    activity
                        .findNavController(R.id.fragment_container)
                        .navigate(R.id.detailListFragment, bundleOf("type" to home.homeSection))
                }
            }
            is PlaylistViewHolder -> {
                val home = list[position]
                holder.bindView(home)
                holder.clickableArea.setOnClickListener {
                    it.findFragment<HomeFragment>().setSharedAxisXTransitions()
                    activity
                        .findNavController(R.id.fragment_container)
                        .navigate(R.id.detailListFragment, bundleOf("type" to home.homeSection))
                }
            }
        /*            is EmptyViewHolder -> {
            holder.bindView()
            holder.browseButton.setOnClickListener {
                //empty
            }
        }*/
        // 懒得写了
        }
    }

    override fun getItemCount(): Int {
        return if (list.isEmpty()) 1 else list.size // 空状态时显示一个项目
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapData(sections: List<Home>) {
        list = sections
        notifyDataSetChanged()
    }

    // 空状态视图持有者
    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /*        val browseButton: View = view.findViewById(R.id.browseButton)

        fun bindView() {
            // 可以在这里添加动态文本或图标
        }*/
    }

    @Suppress("UNCHECKED_CAST")
    private inner class AlbumViewHolder(view: View) : AbsHomeViewItem(view) {
        fun bindView(home: Home) {
            title.setText(home.titleRes)
            recyclerView.apply {
                adapter = albumAdapter(home.arrayList as List<Album>)
                layoutManager = gridLayoutManager()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inner class ArtistViewHolder(view: View) : AbsHomeViewItem(view) {
        fun bindView(home: Home) {
            title.setText(home.titleRes)
            recyclerView.apply {
                layoutManager = linearLayoutManager()
                adapter = artistsAdapter(home.arrayList as List<Artist>)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inner class PlaylistViewHolder(view: View) : AbsHomeViewItem(view) {
        fun bindView(home: Home) {
            title.setText(home.titleRes)
            recyclerView.apply {
                val songAdapter =
                    SongAdapter(
                        activity,
                        home.arrayList as MutableList<Song>,
                        R.layout.item_favourite_card,
                    )
                layoutManager = linearLayoutManager()
                adapter = songAdapter
            }
        }
    }

    open class AbsHomeViewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
        val title: AppCompatTextView = itemView.findViewById(R.id.title)
        val clickableArea: ViewGroup = itemView.findViewById(R.id.clickable_area)
    }

    private fun artistsAdapter(artists: List<Artist>) =
        ArtistAdapter(activity, artists, PreferenceUtil.homeArtistGridStyle, this)

    private fun albumAdapter(albums: List<Album>) =
        AlbumAdapter(activity, albums, PreferenceUtil.homeAlbumGridStyle, this)

    private fun gridLayoutManager() =
        GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)

    private fun linearLayoutManager() =
        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

    override fun onArtist(artistId: Long, view: View) {
        activity
            .findNavController(R.id.fragment_container)
            .navigate(
                R.id.artistDetailsFragment,
                bundleOf(EXTRA_ARTIST_ID to artistId),
                null,
                FragmentNavigatorExtras(view to artistId.toString()),
            )
    }

    override fun onAlbumClick(albumId: Long, view: View) {
        activity
            .findNavController(R.id.fragment_container)
            .navigate(
                R.id.albumDetailsFragment,
                bundleOf(EXTRA_ALBUM_ID to albumId),
                null,
                FragmentNavigatorExtras(view to albumId.toString()),
            )
    }
}
