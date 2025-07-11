package zzh.lifeplayer.music.glide.playlistPreview

import android.content.Context
import android.graphics.Bitmap
import zzh.lifeplayer.music.util.AutoGeneratedPlaylistBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class PlaylistPreviewFetcher(val context: Context, private val playlistPreview: PlaylistPreview) :
    DataFetcher<Bitmap>, CoroutineScope by GlideScope() {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        launch {
            try {
                val bitmap =
                    AutoGeneratedPlaylistBitmap.getBitmap(
                        context,
                        playlistPreview.songs
                    )
                callback.onDataReady(bitmap)
            } catch (e: Exception) {
                callback.onLoadFailed(e)
            }
        }
    }

    override fun cleanup() {}

    override fun cancel() {
        cancel(null)
    }

    override fun getDataClass(): Class<Bitmap> {
        return Bitmap::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }
}

private val glideDispatcher: CoroutineDispatcher by lazy {
    Executors.newFixedThreadPool(4).asCoroutineDispatcher()
}

@Suppress("FunctionName")
internal fun GlideScope(): CoroutineScope = CoroutineScope(SupervisorJob() + glideDispatcher)