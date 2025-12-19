package zzh.lifeplayer.music.model

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import zzh.lifeplayer.music.repository.LastAddedRepository
import zzh.lifeplayer.music.repository.SongRepository
import zzh.lifeplayer.music.repository.TopPlayedRepository

abstract class AbsCustomPlaylist(id: Long, name: String) : Playlist(id, name), KoinComponent {

    abstract fun songs(): List<Song>

    protected val songRepository by inject<SongRepository>()

    protected val topPlayedRepository by inject<TopPlayedRepository>()

    protected val lastAddedRepository by inject<LastAddedRepository>()
}
