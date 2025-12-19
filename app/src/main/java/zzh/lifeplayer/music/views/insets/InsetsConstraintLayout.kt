package zzh.lifeplayer.music.views.insets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import dev.chrisbanes.insetter.applyInsetter
import zzh.lifeplayer.music.util.PreferenceUtil

class InsetsConstraintLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        if (!isInEditMode && !PreferenceUtil.isFullScreenMode)
            applyInsetter { type(navigationBars = true) { padding(vertical = true) } }
    }
}
