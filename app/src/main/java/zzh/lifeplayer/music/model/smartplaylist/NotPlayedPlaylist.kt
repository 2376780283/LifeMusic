package zzh.lifeplayer.music.model.smartplaylist

import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.model.Song
import kotlinx.parcelize.Parcelize

@Parcelize
class NotPlayedPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.not_recently_played),
    iconRes = R.drawable.ic_audiotrack
) {
    override fun songs(): List<Song> {
        return topPlayedRepository.notRecentlyPlayedTracks()
    }
}