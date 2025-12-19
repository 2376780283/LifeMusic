package zzh.lifeplayer.appthemehelper.util

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

/** @author Hemanth S (h4h13). */
object VersionUtils {
    /** @return true if device is running API >= 25 */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
    fun hasNougatMR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

    /** @return true if device is running API >= 26 */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun hasOreo(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /** @return true if device is running API >= 27 */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
    fun hasOreoMR1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

    /** @return true if device is running API >= 28 */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    fun hasP(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    /** @return true if device is running API >= 29 */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    @JvmStatic
    fun hasQ(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /** @return true if device is running API >= 30 */
    // this app  support at least for android 11

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    @JvmStatic
    fun hasR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    /** @return true if device is running API >= 31 */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    @JvmStatic
    fun hasS(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    /** @return true if device is running API >= 33 */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    @JvmStatic
    fun hasT(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}
