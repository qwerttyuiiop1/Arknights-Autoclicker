package com.example.arknightsautoclicker.andorid

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings

/**
 * request necessary permissions that needs an activity
 */
class PermissionLauncher : AppCompatActivity() {
    companion object {
        const val REQUEST_OVERLAY_PERMISSION = 100
        const val REQUEST_MEDIA_PROJECTION = 101
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
            return
        }
        if (!checkAccessibility()) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return
        }
        val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
    }
    private fun checkAccessibility(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val packageName = packageName
        val className = AutoclickService::class.java.name
        return enabledServices != null && "$packageName/$className" in enabledServices
    }
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_MEDIA_PROJECTION -> {
                if (resultCode == Activity.RESULT_OK) {
                    val i = Intent(this, AutoclickService::class.java)
                        .setAction(AutoclickService.ACTION_INIT_PERMISSION)
                        .putExtra(AutoclickService.EXTRA_RESULT_CODE, resultCode)
                        .putExtra(AutoclickService.EXTRA_RESULT_DATA, data!!)
                        .putExtras(intent)
                    startForegroundService(i)
                    finishAndRemoveTask()
                } else {
                    finish()
                }
            }
        }
    }

}