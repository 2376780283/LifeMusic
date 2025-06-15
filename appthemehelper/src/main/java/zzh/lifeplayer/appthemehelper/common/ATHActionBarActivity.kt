package zzh.lifeplayer.appthemehelper.common

import androidx.appcompat.widget.Toolbar

import zzh.lifeplayer.appthemehelper.util.ToolbarContentTintHelper

class ATHActionBarActivity : ATHToolbarActivity() {

    override fun getATHToolbar(): Toolbar? {
        return ToolbarContentTintHelper.getSupportActionBarView(supportActionBar)
    }
}
