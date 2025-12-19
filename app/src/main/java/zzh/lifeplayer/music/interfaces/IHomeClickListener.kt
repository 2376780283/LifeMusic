package zzh.lifeplayer.music.interfaces

import zzh.lifeplayer.music.model.Album
import zzh.lifeplayer.music.model.Artist
import zzh.lifeplayer.music.model.Genre

interface IHomeClickListener {
    fun onAlbumClick(album: Album)

    fun onArtistClick(artist: Artist)

    fun onGenreClick(genre: Genre)
}
