package zzh.lifeplayer.appthemehelper.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.google.android.material.materialswitch.MaterialSwitch
import zzh.lifeplayer.appthemehelper.ATH
import zzh.lifeplayer.appthemehelper.ThemeStore

/** @author Aidan Follestad (afollestad) */
class ATESwitch
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) :
    MaterialSwitch(context, attrs, defStyleAttr) {
    init {
        if (!isInEditMode && !ThemeStore.isMD3Enabled(context)) {
            ATH.setTint(this, ThemeStore.accentColor(context))
        }
    }

    override fun isShown(): Boolean = parent != null && isVisible
}
