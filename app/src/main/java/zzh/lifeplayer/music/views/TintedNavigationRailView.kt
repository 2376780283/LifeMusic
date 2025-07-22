package zzh.lifeplayer.music.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import zzh.lifeplayer.appthemehelper.util.ATHUtil
import zzh.lifeplayer.music.extensions.accentColor
import zzh.lifeplayer.music.extensions.addAlpha
import zzh.lifeplayer.music.extensions.setItemColors
import zzh.lifeplayer.music.util.PreferenceUtil
import com.google.android.material.navigationrail.NavigationRailView

class TintedNavigationRailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : NavigationRailView(context, attrs, defStyleAttr) {

    init {
        if (!isInEditMode) {
            labelVisibilityMode = PreferenceUtil.tabTitleMode

            if (!PreferenceUtil.materialYou) {
                val iconColor = ATHUtil.resolveColor(context, android.R.attr.colorControlNormal)
                val accentColor = context.accentColor()
                setItemColors(iconColor, accentColor)
                itemRippleColor = ColorStateList.valueOf(accentColor.addAlpha(0.08F))
                itemActiveIndicatorColor = ColorStateList.valueOf(accentColor.addAlpha(0.12F))
            }
        }
    }
}