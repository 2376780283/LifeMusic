@file:JvmName("PermissionUtils")

package zzh.lifeplayer.music.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val TAG = "PermissionUtils"

const val CODE_RW_PERMISSION = 78

// 移除 @JvmStatic 注解，顶级函数默认就是静态的
fun hasRWPermission(context: Context): Boolean {
    val read =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED

    val write =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED
    return read && write
}

/** 请求储存权限(Android 6-10)/所有文件访问权限(Android 11+) */
// 移除 @JvmStatic 注解
fun requestAllFilesPermission(
    context: Context,
    launcher: ActivityResultLauncher<Intent>,
    onGrant: () -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            try {
                val intent =
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                // 修复字符串连接问题
                val packageName = context.packageName
                intent.data = Uri.parse("package:$packageName")
                launcher.launch(intent)
            } catch (e: Exception) {
                val intent =
                    Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                launcher.launch(intent)
            }
        } else {
            onGrant()
            Log.d(TAG, "requestAllFilesPermission: 已拥有所有文件访问权限")
        }
    } else {
        // Android 10及以下用普通方式
        if (!hasRWPermission(context)) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
                CODE_RW_PERMISSION,
            )
        } else {
            Log.d(TAG, "requestAllFilesPermission: 已拥有文件读写权限")
            onGrant()
        }
    }
}
