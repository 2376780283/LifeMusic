package zzh.lifeplayer.music.model.smartplaylist

import kotlinx.parcelize.Parcelize
import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.model.Song

@Parcelize
class LastAddedPlaylist :
    AbsSmartPlaylist(
        name = App.getContext().getString(R.string.last_added),
        iconRes = R.drawable.ic_library_add,
    ) {
    override fun songs(): List<Song> {
        return lastAddedRepository.recentSongs()
    }
}
