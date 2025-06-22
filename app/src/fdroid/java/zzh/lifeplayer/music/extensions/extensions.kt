@file:Suppress("UNUSED_PARAMETER", "unused")

package zzh.lifeplayer.music.extensions

import android.content.Context
import android.view.Menu
import androidx.fragment.app.FragmentActivity


fun FragmentActivity.installLanguageAndRecreate(code: String, onInstallComplete: () -> Unit) {
    onInstallComplete()
}

fun Context.goToProVersion() {}

fun Context.installSplitCompat() {}