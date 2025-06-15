package zzh.lifeplayer.music.model.smartplaylist

import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.model.Song
import kotlinx.parcelize.Parcelize

@Parcelize
class ShuffleAllPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.action_shuffle_all),
    iconRes = R.drawable.ic_shuffle
) {
    override fun songs(): List<Song> {
        return songRepository.songs()
    }
}