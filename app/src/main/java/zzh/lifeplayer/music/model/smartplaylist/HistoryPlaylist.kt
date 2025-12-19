package zzh.lifeplayer.music.model.smartplaylist

import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent
import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.model.Song

@Parcelize
class HistoryPlaylist :
    AbsSmartPlaylist(
        name = App.getContext().getString(R.string.history),
        iconRes = R.drawable.ic_history,
    ),
    KoinComponent {

    override fun songs(): List<Song> {
        return topPlayedRepository.recentlyPlayedTracks()
    }
}
