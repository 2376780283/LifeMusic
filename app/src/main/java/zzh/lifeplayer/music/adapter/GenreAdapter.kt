package zzh.lifeplayer.music.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*
import zzh.lifeplayer.music.R
import zzh.lifeplayer.music.databinding.ItemGenreBinding
import zzh.lifeplayer.music.glide.LifeGlideExtension
import zzh.lifeplayer.music.glide.LifeGlideExtension.asBitmapPalette
import zzh.lifeplayer.music.glide.LifeGlideExtension.songCoverOptions
import zzh.lifeplayer.music.glide.LifeMusicColoredTarget
import zzh.lifeplayer.music.interfaces.IGenreClickListener
import zzh.lifeplayer.music.model.Genre
import zzh.lifeplayer.music.util.MusicUtil
import zzh.lifeplayer.music.util.color.MediaNotificationProcessor

/** @author Hemanth S (h4h13). */
class GenreAdapter(
    private val activity: FragmentActivity,
    var dataSet: List<Genre>,
    private val listener: IGenreClickListener,
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    init {
        this.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemGenreBinding.inflate(LayoutInflater.from(activity), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = dataSet[position]
        holder.binding.title.text = genre.name
        holder.binding.text.text =
            String.format(
                Locale.getDefault(),
                "%d %s",
                genre.songCount,
                if (genre.songCount > 1) activity.getString(R.string.songs)
                else activity.getString(R.string.song),
            )
        loadGenreImage(genre, holder)
    }

    private fun loadGenreImage(genre: Genre, holder: GenreAdapter.ViewHolder) {
        val genreSong = MusicUtil.songByGenre(genre.id)
        Glide.with(activity)
            .asBitmapPalette()
            .songCoverOptions(genreSong)
            .load(LifeGlideExtension.getSongModel(genreSong))
            .into(
                object : LifeMusicColoredTarget(holder.binding.image) {
                    override fun onColorReady(colors: MediaNotificationProcessor) {
                        setColors(holder, colors)
                    }
                }
            )
        // Just for a bit of shadow around image
        holder.binding.image.outlineProvider = ViewOutlineProvider.BOUNDS
    }

    private fun setColors(holder: ViewHolder, color: MediaNotificationProcessor) {
        holder.binding.imageContainerCard.setCardBackgroundColor(color.backgroundColor)
        holder.binding.title.setTextColor(color.primaryTextColor)
        holder.binding.text.setTextColor(color.secondaryTextColor)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(list: List<Genre>) {
        dataSet = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemGenreBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(v: View?) {
            listener.onClickGenre(dataSet[layoutPosition], itemView)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}
