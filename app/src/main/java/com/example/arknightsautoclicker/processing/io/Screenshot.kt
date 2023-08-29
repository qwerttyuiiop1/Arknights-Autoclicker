package com.example.arknightsautoclicker.processing.io

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.view.WindowManager
import android.view.WindowMetrics
import com.example.arknightsautoclicker.andorid.AutoclickService
import com.example.arknightsautoclicker.processing.components.RecyclableBitmap
import com.example.arknightsautoclicker.processing.ext.rgbaToBitmap

/**
 * class for taking screenshots, automatically handles configuration changes
 */
class Screenshot(
    private val context: Context,
    intent: Intent
): AutoCloseable {
    companion object {
        private const val TAG = "ScreenshotTag"
    }

    private val mediaProjectionManager: MediaProjectionManager
    private val windowManager: WindowManager

    private val projection: MediaProjection
    private var virtualDisplay: VirtualDisplay? = null
    private val configChangeReceiver: BroadcastReceiver
    var imageReader: ImageReader? = null
        private set

    private var bitmap = RecyclableBitmap()
    private var width: Int = 0
    private var height: Int = 0

    var preprocess: Preprocess? = null

    val latestRawImage: Image?
        get() = imageReader!!.acquireLatestImage()
    val latestRawBitmap: Bitmap?
        get() {
            val image = latestRawImage ?: return null
            image.use { it.rgbaToBitmap(bitmap) }
            return bitmap.get()
        }
    val latestBitmap: Bitmap?
        get() {
            val b = latestRawBitmap ?: return null
            preprocess?.applyTo(b)
            return b
        }

    init {
        mediaProjectionManager =
            context.getSystemService(MediaProjectionManager::class.java)
        windowManager =
            context.getSystemService(WindowManager::class.java)

        val resultCode = intent.getIntExtra(AutoclickService.EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
        val resultData = intent.getParcelableExtra<Intent>(AutoclickService.EXTRA_RESULT_DATA)!!
        projection = mediaProjectionManager.getMediaProjection(resultCode, resultData)

        val intentFilter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        configChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED)
                    configureVirtualDisplay(windowManager.maximumWindowMetrics)
            }
        }
        context.registerReceiver(configChangeReceiver, intentFilter)

        configureVirtualDisplay(windowManager.maximumWindowMetrics)
    }

    fun configureVirtualDisplay(metrics: WindowMetrics) {
        virtualDisplay?.release()
        imageReader?.close()

        width = metrics.bounds.width()
        height = metrics.bounds.height()

        // ??? ImageFormat.YUV_420_888
        @SuppressLint("WrongConstant")
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        virtualDisplay = projection.createVirtualDisplay(
            TAG,
            width, height,
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null, null
        )
    }

    override fun close() {
        imageReader?.close()
        virtualDisplay?.release()
        //bitmap?.recycle()
        projection.stop()
        context.unregisterReceiver(configChangeReceiver)

        imageReader = null
        virtualDisplay = null
        bitmap.bitmap = null
    }
}