package zzh.lifeplayer.music.fragments.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import zzh.lifeplayer.appthemehelper.ThemeStore
import zzh.lifeplayer.appthemehelper.util.VersionUtils
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.appshortcuts.DynamicShortcutManager
import zzh.lifeplayer.music.databinding.FragmentSettingsBinding
import zzh.lifeplayer.music.extensions.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback

class SettingsFragment : Fragment(R.layout.fragment_settings), ColorCallback {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSettingsBinding.bind(view)
        setupToolbar()
    }

    private fun setupToolbar() {
        val navController: NavController = findNavController(R.id.contentFrame)
        with (binding.appBarLayout.toolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back)
            isTitleCentered = false
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.appBarLayout.title =
                navController.currentDestination?.let { getStringFromDestination(it) }.toString()
        }
    }

    private fun getStringFromDestination(currentDestination: NavDestination): String {
        val idRes = when (currentDestination.id) {
            R.id.mainSettingsFragment -> R.string.action_settings
            R.id.audioSettings -> R.string.pref_header_audio
            R.id.imageSettingFragment -> R.string.pref_header_images
            R.id.notificationSettingsFragment -> R.string.notification
            R.id.nowPlayingSettingsFragment -> R.string.now_playing
            R.id.otherSettingsFragment -> R.string.others
            R.id.personalizeSettingsFragment -> R.string.personalize
            R.id.themeSettingsFragment -> R.string.general_settings_title
            R.id.aboutActivity -> R.string.action_about
            R.id.backup_fragment -> R.string.backup_restore_title
            else -> R.id.action_settings
        }
        return getString(idRes)
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        ThemeStore.editTheme(requireContext()).accentColor(color).commit()
        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(requireContext()).updateDynamicShortcuts()
        activity?.recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = SettingsFragment::class.java.simpleName
    }
}
