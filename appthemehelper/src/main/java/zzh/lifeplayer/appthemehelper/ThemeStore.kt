package zzh.lifeplayer.appthemehelper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import zzh.lifeplayer.appthemehelper.util.ATHUtil.isWindowBackgroundDark
import zzh.lifeplayer.appthemehelper.util.ATHUtil.resolveColor
import zzh.lifeplayer.appthemehelper.util.ColorUtil
import zzh.lifeplayer.appthemehelper.util.VersionUtils

/** @author Aidan Follestad (afollestad), Karim Abou Zeid (kabouzeid) */
class ThemeStore private constructor(private val mContext: Context) :
    ThemeStorePrefKeys, ThemeStoreInterface {
    private val mEditor: SharedPreferences.Editor = prefs(mContext).edit()

    override fun activityTheme(@StyleRes theme: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_ACTIVITY_THEME, theme)
        return this
    }

    override fun primaryColor(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_PRIMARY_COLOR, color)
        if (autoGeneratePrimaryDark(mContext)) {
            primaryColorDark(ColorUtil.darkenColor(color))
        }
        return this
    }

    override fun primaryColorRes(@ColorRes colorRes: Int): ThemeStore =
        primaryColor(ContextCompat.getColor(mContext, colorRes))

    override fun primaryColorAttr(@AttrRes colorAttr: Int): ThemeStore =
        primaryColor(resolveColor(mContext, colorAttr))

    override fun primaryColorDark(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_PRIMARY_COLOR_DARK, color)
        return this
    }

    override fun primaryColorDarkRes(@ColorRes colorRes: Int): ThemeStore =
        primaryColorDark(ContextCompat.getColor(mContext, colorRes))

    override fun primaryColorDarkAttr(@AttrRes colorAttr: Int): ThemeStore =
        primaryColorDark(resolveColor(mContext, colorAttr))

    override fun accentColor(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_ACCENT_COLOR, color)
        return this
    }

    override fun wallpaperColor(context: Context, color: Int): ThemeStore {
        if (ColorUtil.isColorLight(color)) {
            mEditor.putInt(ThemeStorePrefKeys.KEY_WALLPAPER_COLOR_DARK, color)
            mEditor.putInt(
                ThemeStorePrefKeys.KEY_WALLPAPER_COLOR_LIGHT,
                ColorUtil.getReadableColorLight(color, Color.WHITE),
            )
        } else {
            mEditor.putInt(ThemeStorePrefKeys.KEY_WALLPAPER_COLOR_LIGHT, color)
            mEditor.putInt(
                ThemeStorePrefKeys.KEY_WALLPAPER_COLOR_DARK,
                ColorUtil.getReadableColorDark(color, Color.parseColor("#ffa2c27d")),
            )
        }
        return this
    }

    override fun accentColorRes(@ColorRes colorRes: Int): ThemeStore =
        accentColor(ContextCompat.getColor(mContext, colorRes))

    override fun accentColorAttr(@AttrRes colorAttr: Int): ThemeStore =
        accentColor(resolveColor(mContext, colorAttr))

    override fun statusBarColor(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_STATUS_BAR_COLOR, color)
        return this
    }

    override fun statusBarColorRes(@ColorRes colorRes: Int): ThemeStore =
        statusBarColor(ContextCompat.getColor(mContext, colorRes))

    override fun statusBarColorAttr(@AttrRes colorAttr: Int): ThemeStore =
        statusBarColor(resolveColor(mContext, colorAttr))

    override fun navigationBarColor(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_NAVIGATION_BAR_COLOR, color)
        return this
    }

    // Commit method

    override fun navigationBarColorRes(@ColorRes colorRes: Int): ThemeStore =
        navigationBarColor(ContextCompat.getColor(mContext, colorRes))

    // Static getters

    override fun navigationBarColorAttr(@AttrRes colorAttr: Int): ThemeStore =
        navigationBarColor(resolveColor(mContext, colorAttr))

    override fun textColorPrimary(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_TEXT_COLOR_PRIMARY, color)
        return this
    }

    override fun textColorPrimaryRes(@ColorRes colorRes: Int): ThemeStore =
        textColorPrimary(ContextCompat.getColor(mContext, colorRes))

    override fun textColorPrimaryAttr(@AttrRes colorAttr: Int): ThemeStore =
        textColorPrimary(resolveColor(mContext, colorAttr))

    override fun textColorPrimaryInverse(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_TEXT_COLOR_PRIMARY_INVERSE, color)
        return this
    }

    override fun textColorPrimaryInverseRes(@ColorRes colorRes: Int): ThemeStore =
        textColorPrimaryInverse(ContextCompat.getColor(mContext, colorRes))

    override fun textColorPrimaryInverseAttr(@AttrRes colorAttr: Int): ThemeStore =
        textColorPrimaryInverse(resolveColor(mContext, colorAttr))

    override fun textColorSecondary(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_TEXT_COLOR_SECONDARY, color)
        return this
    }

    override fun textColorSecondaryRes(@ColorRes colorRes: Int): ThemeStore =
        textColorSecondary(ContextCompat.getColor(mContext, colorRes))

    override fun textColorSecondaryAttr(@AttrRes colorAttr: Int): ThemeStore =
        textColorSecondary(resolveColor(mContext, colorAttr))

    override fun textColorSecondaryInverse(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_TEXT_COLOR_SECONDARY_INVERSE, color)
        return this
    }

    override fun textColorSecondaryInverseRes(@ColorRes colorRes: Int): ThemeStore =
        textColorSecondaryInverse(ContextCompat.getColor(mContext, colorRes))

    override fun textColorSecondaryInverseAttr(@AttrRes colorAttr: Int): ThemeStore =
        textColorSecondaryInverse(resolveColor(mContext, colorAttr))

    override fun coloredStatusBar(colored: Boolean): ThemeStore {
        mEditor.putBoolean(ThemeStorePrefKeys.KEY_APPLY_PRIMARYDARK_STATUSBAR, colored)
        return this
    }

    override fun coloredNavigationBar(applyToNavBar: Boolean): ThemeStore {
        mEditor.putBoolean(ThemeStorePrefKeys.KEY_APPLY_PRIMARY_NAVBAR, applyToNavBar)
        return this
    }

    override fun autoGeneratePrimaryDark(autoGenerate: Boolean): ThemeStore {
        mEditor.putBoolean(ThemeStorePrefKeys.KEY_AUTO_GENERATE_PRIMARYDARK, autoGenerate)
        return this
    }

    override fun commit() {
        mEditor
            .putLong(ThemeStorePrefKeys.VALUES_CHANGED, System.currentTimeMillis())
            .putBoolean(ThemeStorePrefKeys.IS_CONFIGURED_KEY, true)
            .commit()
    }

    companion object {
        fun editTheme(context: Context): ThemeStore = ThemeStore(context)

        @CheckResult
        fun prefs(context: Context): SharedPreferences =
            context.getSharedPreferences(
                ThemeStorePrefKeys.CONFIG_PREFS_KEY_DEFAULT,
                Context.MODE_PRIVATE,
            )

        fun markChanged(context: Context) {
            ThemeStore(context).commit()
        }

        @CheckResult
        @StyleRes
        fun activityTheme(context: Context): Int =
            prefs(context).getInt(ThemeStorePrefKeys.KEY_ACTIVITY_THEME, 0)

        @CheckResult
        @ColorInt
        fun primaryColor(context: Context): Int =
            prefs(context)
                .getInt(
                    ThemeStorePrefKeys.KEY_PRIMARY_COLOR,
                    resolveColor(
                        context,
                        androidx.appcompat.R.attr.colorPrimary,
                        Color.parseColor("#455A64"),
                    ),
                )

        @CheckResult
        @ColorInt
        fun accentColor(context: Context): Int {
            // Set MD3 accent if MD3 is enabled or in-app accent otherwise
            if (isMD3Enabled(context) && VersionUtils.hasS()) {
                return ContextCompat.getColor(context, R.color.m3_accent_color)
            }
            val desaturatedColor = prefs(context).getBoolean("desaturated_color", false)
            val color =
                if (isWallpaperAccentEnabled(context)) {
                    wallpaperColor(context, isWindowBackgroundDark(context))
                } else {
                    prefs(context)
                        .getInt(
                            ThemeStorePrefKeys.KEY_ACCENT_COLOR,
                            resolveColor(
                                context,
                                androidx.appcompat.R.attr.colorAccent,
                                Color.parseColor("#263238"),
                            ),
                        )
                }
            return if (isWindowBackgroundDark(context) && desaturatedColor) {
                ColorUtil.desaturateColor(color, 0.4f)
            } else {
                color
            }
        }

        @CheckResult
        @ColorInt
        fun wallpaperColor(context: Context, isDarkMode: Boolean): Int =
            prefs(context)
                .getInt(
                    if (isDarkMode) ThemeStorePrefKeys.KEY_WALLPAPER_COLOR_DARK
                    else ThemeStorePrefKeys.KEY_WALLPAPER_COLOR_LIGHT,
                    resolveColor(
                        context,
                        androidx.appcompat.R.attr.colorAccent,
                        Color.parseColor("#263238"),
                    ),
                )

        @CheckResult
        @ColorInt
        fun navigationBarColor(context: Context): Int =
            if (!coloredNavigationBar(context)) {
                Color.BLACK
            } else {
                prefs(context)
                    .getInt(ThemeStorePrefKeys.KEY_NAVIGATION_BAR_COLOR, primaryColor(context))
            }

        @CheckResult
        fun coloredStatusBar(context: Context): Boolean =
            prefs(context).getBoolean(ThemeStorePrefKeys.KEY_APPLY_PRIMARYDARK_STATUSBAR, true)

        @CheckResult
        fun coloredNavigationBar(context: Context): Boolean =
            prefs(context).getBoolean(ThemeStorePrefKeys.KEY_APPLY_PRIMARY_NAVBAR, false)

        @CheckResult
        fun autoGeneratePrimaryDark(context: Context): Boolean =
            prefs(context).getBoolean(ThemeStorePrefKeys.KEY_AUTO_GENERATE_PRIMARYDARK, true)

        @CheckResult
        fun isConfigured(context: Context): Boolean =
            prefs(context).getBoolean(ThemeStorePrefKeys.IS_CONFIGURED_KEY, false)

        @CheckResult
        @ColorInt
        fun textColorPrimary(context: Context): Int =
            prefs(context)
                .getInt(
                    ThemeStorePrefKeys.KEY_TEXT_COLOR_PRIMARY,
                    resolveColor(context, android.R.attr.textColorPrimary),
                )

        @CheckResult
        @ColorInt
        fun textColorPrimaryInverse(context: Context): Int =
            prefs(context)
                .getInt(
                    ThemeStorePrefKeys.KEY_TEXT_COLOR_PRIMARY_INVERSE,
                    resolveColor(context, android.R.attr.textColorPrimaryInverse),
                )

        @CheckResult
        @ColorInt
        fun textColorSecondary(context: Context): Int =
            prefs(context)
                .getInt(
                    ThemeStorePrefKeys.KEY_TEXT_COLOR_SECONDARY,
                    resolveColor(context, android.R.attr.textColorSecondary),
                )

        @CheckResult
        @ColorInt
        fun textColorSecondaryInverse(context: Context): Int =
            prefs(context)
                .getInt(
                    ThemeStorePrefKeys.KEY_TEXT_COLOR_SECONDARY_INVERSE,
                    resolveColor(context, android.R.attr.textColorSecondaryInverse),
                )

        fun isConfigured(
            context: Context,
            @IntRange(from = 0, to = Integer.MAX_VALUE.toLong()) version: Int,
        ): Boolean {
            val prefs = prefs(context)
            val lastVersion = prefs.getInt(ThemeStorePrefKeys.IS_CONFIGURED_VERSION_KEY, -1)
            if (version > lastVersion) {
                prefs.edit { putInt(ThemeStorePrefKeys.IS_CONFIGURED_VERSION_KEY, version) }
                return false
            }
            return true
        }

        fun isMD3Enabled(context: Context): Boolean =
            PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(ThemeStorePrefKeys.KEY_MATERIAL_YOU, VersionUtils.hasS())

        private fun isWallpaperAccentEnabled(context: Context): Boolean =
            PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("wallpaper_accent", false)
    }
}
