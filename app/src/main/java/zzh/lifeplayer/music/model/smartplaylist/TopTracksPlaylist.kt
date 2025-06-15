package zzh.lifeplayer.music.model.smartplaylist

import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.model.Song
import kotlinx.parcelize.Parcelize

@Parcelize
class TopTracksPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.my_top_tracks),
    iconRes = R.drawable.ic_trending_up
) {
    override fun songs(): List<Song> {
        return topPlayedRepository.topTracks()
    }
}