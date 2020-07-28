package com.livetyping.permission

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings.*
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

internal class ActivePermissionRequest(
    private val rationaleText: String,
    private val settingsButtonText: String,
    resultListener: (Map<String, Boolean>) -> Unit
) : PermissionRequest(resultListener) {

    private var rationaleShowed = false

    override fun onPermissionsDenied(activity: Activity, @StyleRes themeResId: Int) {
        if (hasDeniedByUserPermissions(activity)) {
            showRequestPermissionDialog(activity, themeResId)
            rationaleShowed = true
        } else {
            val deniedPermissions = getDeniedPermissions(activity).toTypedArray()
            ActivityCompat.requestPermissions(activity, deniedPermissions, requestCode)
        }
    }

    private fun showRequestPermissionDialog(activity: Activity, @StyleRes themeResId: Int) {
        AlertDialog.Builder(activity, themeResId)
            .setMessage(rationaleText)
            .setPositiveButton(android.R.string.ok) { _, _ -> requestPermission(activity) }
            .show()
    }

    override fun afterRequest(activity: Activity, @StyleRes themeResId: Int) {
        val isAllPermissionsGrantedOrRationalShowed =
            rationaleShowed || areAllPermissionsGranted(activity)
        if (isAllPermissionsGrantedOrRationalShowed) {
            invokeResult(activity)
        } else {
            if (!rationaleShowed && getPermissionsWithoutRationale(activity).isNotEmpty()) {
                showOpenSettingsDialog(activity, themeResId)
            } else {
                invokeResult(activity)
            }
        }
    }

    override fun afterSettingsActivityResult(
        activity: Activity,
        @StyleRes themeResId: Int
    ) {
        invokeResult(activity)
    }

    private fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(),
            requestCode
        )
    }

    private fun showOpenSettingsDialog(activity: Activity, @StyleRes themeResId: Int) {
        AlertDialog.Builder(activity, themeResId)
            .setMessage(rationaleText)
            .setPositiveButton(settingsButtonText) { _, _ -> openAppSettings(activity) }
            .setNegativeButton(android.R.string.cancel) { _, _ -> invokeResult(activity) }
            .setOnCancelListener { invokeResult(activity) }
            .show()
    }

    private fun openAppSettings(activity: Activity) {
        val intent = Intent(
            ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${activity.packageName}")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        activity.startActivityForResult(intent, requestCode)
    }
}
