package zzh.lifeplayer.music.extensions

import androidx.core.view.WindowInsetsCompat
import zzh.lifeplayer.music.util.PreferenceUtil
import zzh.lifeplayer.music.util.RetroUtil

fun WindowInsetsCompat?.getBottomInsets(): Int {
    return if (PreferenceUtil.isFullScreenMode) {
        return 0
    } else {
        this?.getInsets(WindowInsetsCompat.Type.systemBars())?.bottom ?: RetroUtil.navigationBarHeight
    }
}
