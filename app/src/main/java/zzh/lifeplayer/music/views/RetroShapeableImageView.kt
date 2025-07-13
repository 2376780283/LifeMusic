package zzh.lifeplayer.music.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import zzh.lifeplayer.music.R

class RetroShapeableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {

    private var lastWidth = -1
    private var lastHeight = -1

    init {
        context.withStyledAttributes(attrs, R.styleable.RetroShapeableImageView, defStyleAttr, 0) {
            val strokeColorResId = getResourceId(
                R.styleable.RetroShapeableImageView_retroStrokeColor,
                R.color.stroke_white
            )
            strokeColor = ContextCompat.getColorStateList(context, strokeColorResId)
        }

        strokeWidth = context.resources.displayMetrics.density * 5
        clipToOutline = true

        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateCornerRadiusIfNeeded()
        }
    }

    private fun updateCornerRadiusIfNeeded() {
        if (width == lastWidth && height == lastHeight) return
        lastWidth = width
        lastHeight = height

        val radius = (width.coerceAtMost(height)) / 2f
        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
            .setAllCornerSizes(radius)
            .build()
    }
}


/*package zzh.lifeplayer.music.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import zzh.lifeplayer.music.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel


class RetroShapeableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = -1
) : ShapeableImageView(context, attrs, defStyle) {


init {
    context.withStyledAttributes(attrs, R.styleable.RetroShapeableImageView, defStyle, -1) {
        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val radius = width / 2f
            shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(radius)
            // 设置边缘白色描边
            strokeColor = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
            strokeWidth = context.resources.displayMetrics.density * 5 // 6dp 但是2dp 更合适
            
            clipToOutline = true
        }
    }
}

    private fun updateCornerSize(cornerSize: Float) {
        shapeAppearanceModel = ShapeAppearanceModel.Builder()
            .setAllCorners(CornerFamily.ROUNDED, cornerSize)
            .build()
    }

    //For square
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}*/