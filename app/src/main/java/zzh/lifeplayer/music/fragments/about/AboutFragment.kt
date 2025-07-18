package zzh.lifeplayer.music.fragments.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.Constants
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.adapter.ContributorAdapter
import zzh.lifeplayer.music.databinding.FragmentAboutBinding
import zzh.lifeplayer.music.extensions.openUrl
import zzh.lifeplayer.music.fragments.LibraryViewModel
import zzh.lifeplayer.music.util.NavigationUtil
import dev.chrisbanes.insetter.applyInsetter
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AboutFragment : Fragment(R.layout.fragment_about), View.OnClickListener {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel by activityViewModel<LibraryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)
        binding.aboutContent.cardOther.version.setSummary(getAppVersion())
        setUpView()
        loadContributors()

        binding.aboutContent.root.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }
    }

    private fun setUpView() {
    //    binding.aboutContent.cardRetroInfo.appGithub.setOnClickListener(this)
  //      binding.aboutContent.cardRetroInfo.faqLink.setOnClickListener(this)
   //     binding.aboutContent.cardRetroInfo.appRate.setOnClickListener(this)
   //     binding.aboutContent.cardRetroInfo.appTranslation.setOnClickListener(this)
 //       binding.aboutContent.cardRetroInfo.appShare.setOnClickListener(this)
    //    binding.aboutContent.cardRetroInfo.donateLink.setOnClickListener(this)
       binding.aboutContent.cardRetroInfo.bugReportLink.setOnClickListener(this)

  //      binding.aboutContent.cardSocial.telegramLink.setOnClickListener(this)
   //     binding.aboutContent.cardSocial.twitterLink.setOnClickListener(this)
      //  binding.aboutContent.cardSocial.pinterestLink.setOnClickListener(this)
    //    binding.aboutContent.cardSocial.websiteLink.setOnClickListener(this)

   //     binding.aboutContent.cardOther.changelog.setOnClickListener(this)
   //    binding.aboutContent.cardOther.openSource.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
    //        R.id.pinterestLink -> openUrl(Constants.PINTEREST)
     //       R.id.faqLink -> openUrl(Constants.FAQ_LINK)
    //        R.id.telegramLink -> openUrl(Constants.APP_TELEGRAM_LINK)
     //       R.id.appGithub -> openUrl(Constants.GITHUB_PROJECT)
    //        R.id.appTranslation -> openUrl(Constants.TRANSLATE)
     //       R.id.appRate -> openUrl(Constants.RATE_ON_GOOGLE_PLAY)
     //       R.id.appShare -> shareApp()
    //        R.id.donateLink -> NavigationUtil.goToSupportDevelopment(requireActivity())
    //        R.id.twitterLink -> openUrl(Constants.APP_TWITTER_LINK)
     //       R.id.changelog -> NavigationUtil.gotoWhatNews(requireActivity())
       //     R.id.openSource -> NavigationUtil.goToOpenSource(requireActivity())
            R.id.bugReportLink -> NavigationUtil.bugReport(requireActivity())
   //         R.id.websiteLink -> openUrl(Constants.WEBSITE)
        }
    }

    private fun getAppVersion(): String {
        return try {
            val isPro = if (App.isProVersion()) "Pro" else "Free"
            val packageInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            "${packageInfo.versionName} $isPro"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0.0.0"
        }
    }

    private fun shareApp() {
        ShareCompat.IntentBuilder(requireActivity()).setType("text/plain")
            .setChooserTitle(R.string.share_app)
            .setText(String.format(getString(R.string.app_share), requireActivity().packageName))
            .startChooser()
    }

    private fun loadContributors() {
        val contributorAdapter = ContributorAdapter(emptyList())
        binding.aboutContent.cardCredit.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            adapter = contributorAdapter
        }
        libraryViewModel.fetchContributors().observe(viewLifecycleOwner) { contributors ->
            contributorAdapter.swapData(contributors)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
