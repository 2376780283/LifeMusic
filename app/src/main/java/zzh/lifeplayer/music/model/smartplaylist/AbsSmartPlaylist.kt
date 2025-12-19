package zzh.lifeplayer.music.model.smartplaylist

import androidx.annotation.DrawableRes
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.model.AbsCustomPlaylist

abstract class AbsSmartPlaylist(
    name: String,
    @DrawableRes val iconRes: Int = R.drawable.ic_queue_music,
) : AbsCustomPlaylist(id = PlaylistIdGenerator(name, iconRes), name = name)
