package zzh.lifeplayer.music.preferences

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat.SRC_IN
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import zzh.lifeplayer.appthemehelper.common.prefs.supportv7.ATEDialogPreference
import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.databinding.PreferenceDialogNowPlayingScreenBinding
import zzh.lifeplayer.music.databinding.PreferenceNowPlayingScreenItemBinding
import zzh.lifeplayer.music.extensions.*
import zzh.lifeplayer.music.fragments.AlbumCoverStyle
import zzh.lifeplayer.music.fragments.AlbumCoverStyle.*
import zzh.lifeplayer.music.util.PreferenceUtil
import zzh.lifeplayer.music.util.ViewUtil
import com.bumptech.glide.Glide

class AlbumCoverStylePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : ATEDialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    private val mLayoutRes = R.layout.preference_dialog_now_playing_screen

    override fun getDialogLayoutResource(): Int {
        return mLayoutRes
    }

    init {
        icon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                context.colorControlNormal(),
                SRC_IN
            )
    }
}

class AlbumCoverStylePreferenceDialog : DialogFragment(),
    ViewPager.OnPageChangeListener {

    private var viewPagerPosition: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = PreferenceDialogNowPlayingScreenBinding.inflate(layoutInflater)
        binding.nowPlayingScreenViewPager.apply {
            adapter = AlbumCoverStyleAdapter(requireContext())
            addOnPageChangeListener(this@AlbumCoverStylePreferenceDialog)
            pageMargin = ViewUtil.convertDpToPixel(32f, resources).toInt()
            currentItem = PreferenceUtil.albumCoverStyle.ordinal
        }

        return materialDialog(R.string.pref_title_album_cover_style)
            .setPositiveButton(R.string.set) { _, _ ->
                val coverStyle = values()[viewPagerPosition]
                PreferenceUtil.albumCoverStyle = coverStyle              
            }
            .setView(binding.root)
            .create()
            .colorButtons()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        this.viewPagerPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private class AlbumCoverStyleAdapter(private val context: Context) :
        PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val albumCoverStyle = values()[position]

            val inflater = LayoutInflater.from(context)
            val binding = PreferenceNowPlayingScreenItemBinding.inflate(inflater, collection, true)

            Glide.with(context).load(albumCoverStyle.drawableResId).into(binding.image)
            binding.title.setText(albumCoverStyle.titleRes)
                binding.proText.show()
                binding.proText.setText(R.string.pro)
            return binding.root
        }

        override fun destroyItem(
            collection: ViewGroup,
            position: Int,
            view: Any
        ) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return values().size
        }

        override fun isViewFromObject(view: View, instace: Any): Boolean {
            return view === instace
        }

        override fun getPageTitle(position: Int): CharSequence {
            return context.getString(values()[position].titleRes)
        }
    }

    companion object {
        val TAG: String = AlbumCoverStylePreferenceDialog::class.java.simpleName

        fun newInstance(): AlbumCoverStylePreferenceDialog {
            return AlbumCoverStylePreferenceDialog()
        }
    }
}
/* private fun isAlbumCoverStyle(style: AlbumCoverStyle): Boolean {
    return (!App.isProVersion() && (style == Circle || style == Card || style == FullCard))
}*/