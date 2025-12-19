package zzh.lifeplayer.music.interfaces

import android.view.View
import zzh.lifeplayer.music.model.Genre

interface IGenreClickListener {
    fun onClickGenre(genre: Genre, view: View)
}
