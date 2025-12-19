package zzh.lifeplayer.music.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.transition.Transition
import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.R // ← 记得导入你的 tag 资源
import zzh.lifeplayer.music.extensions.colorControlNormal
import zzh.lifeplayer.music.glide.palette.BitmapPaletteTarget
import zzh.lifeplayer.music.glide.palette.BitmapPaletteWrapper
import zzh.lifeplayer.music.util.color.MediaNotificationProcessor

abstract class LifeMusicColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    protected val defaultFooterColor: Int
        get() = getView().context.colorControlNormal()

    abstract fun onColorReady(colors: MediaNotificationProcessor)

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        onColorReady(MediaNotificationProcessor.errorColor(App.getContext()))
    }

    override fun onResourceReady(
        resource: BitmapPaletteWrapper,
        transition: Transition<in BitmapPaletteWrapper>?,
    ) {
        super.onResourceReady(resource, transition)

        // ① 提取颜色回调
        MediaNotificationProcessor(App.getContext())
            .getPaletteAsync({ palette -> onColorReady(palette) }, resource.bitmap)

        // ② 做淡入动画
        val imageView = getView() ?: return
        val alreadyFaded = imageView.getTag(R.id.tag_has_faded_in) == true
        if (!alreadyFaded) {
            imageView.alpha = 0f
            imageView
                .animate()
                .alpha(1f)
                .setDuration(600) // 动画时长 300ms，可调
                .setStartDelay(0)
                .start()
            imageView.setTag(R.id.tag_has_faded_in, true)
        }
    }
}
/*
package zzh.lifeplayer.music.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import zzh.lifeplayer.music.App
import zzh.lifeplayer.music.extensions.colorControlNormal
import zzh.lifeplayer.music.glide.palette.BitmapPaletteTarget
import zzh.lifeplayer.music.glide.palette.BitmapPaletteWrapper
import zzh.lifeplayer.music.util.color.MediaNotificationProcessor
import com.bumptech.glide.request.transition.Transition

abstract class LifeMusicColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    protected val defaultFooterColor: Int
        get() = getView().context.colorControlNormal()

    abstract fun onColorReady(colors: MediaNotificationProcessor)

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        onColorReady(MediaNotificationProcessor.errorColor(App.getContext()))
    }

    override fun onResourceReady(
        resource: BitmapPaletteWrapper,
        transition: Transition<in BitmapPaletteWrapper>?
    ) {
        super.onResourceReady(resource, transition)
        MediaNotificationProcessor(App.getContext()).getPaletteAsync({
            onColorReady(it)
        }, resource.bitmap)
    }
}

*/
