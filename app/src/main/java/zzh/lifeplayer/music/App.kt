package zzh.lifeplayer.music

import android.app.Application
import androidx.preference.PreferenceManager
import cat.ereza.customactivityoncrash.config.CaocConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import zzh.lifeplayer.appthemehelper.ThemeStore
import zzh.lifeplayer.appthemehelper.util.VersionUtils
import zzh.lifeplayer.music.activities.ErrorActivity
import zzh.lifeplayer.music.activities.MainActivity
import zzh.lifeplayer.music.appshortcuts.DynamicShortcutManager
import zzh.lifeplayer.music.helper.WallpaperAccentManager

class App : Application() {

    private val wallpaperAccentManager = WallpaperAccentManager(this)

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(appModules)
        }
        if (!ThemeStore.isConfigured(this, 3)) {
            ThemeStore.editTheme(this)
                .accentColorRes(zzh.lifeplayer.appthemehelper.R.color.md_deep_purple_A200)
                .coloredNavigationBar(true)
                .commit()
        }
        wallpaperAccentManager.init()
        if (VersionUtils.hasNougatMR()) DynamicShortcutManager(this).initDynamicShortcuts()
        CaocConfig.Builder.create()
            .errorActivity(ErrorActivity::class.java)
            .restartActivity(MainActivity::class.java)
            .apply()

        // Set Default values for now playing preferences
        // This will reduce startup time for now playing settings fragment as Preference listener of
        // AbsSlidingMusicPanelActivity won't be called
        PreferenceManager.setDefaultValues(this, R.xml.pref_now_playing_screen, false)
    }

    override fun onTerminate() {
        super.onTerminate()
        wallpaperAccentManager.release()
    }

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }

        fun isProVersion(): Boolean {
            return true
        }
    }
}
