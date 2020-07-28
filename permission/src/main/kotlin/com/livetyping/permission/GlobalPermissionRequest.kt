package com.livetyping.permission

import android.app.Activity
import android.content.Intent
import androidx.annotation.StyleRes
import androidx.core.app.ActivityCompat

internal class GlobalPermissionRequest(
    private val preSettingsClass: Class<out PreSettingsActivity>,
    resultListener: (Map<String, Boolean>) -> Unit
) : PermissionRequest(resultListener) {

    companion object {
        internal const val PERMISSION_REQUEST_CODE_KEY = "PermissionRepository.PermissionCodeKey"
        internal const val PERMISSION_KEY = "PermissionRepository.PermissionKey"
    }

    override fun onPermissionsDenied(activity: Activity, @StyleRes themeResId: Int) {
        val permissions = permissions.toTypedArray()
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    override fun afterRequest(activity: Activity, @StyleRes themeResId: Int) {
        if (areAllPermissionsGranted(activity)) {
            invokeResult(activity)
        } else {
            onNotAllPermissionsGranted(activity)
        }
    }

    private fun onNotAllPermissionsGranted(activity: Activity) {
        val intent = Intent(activity, preSettingsClass)
        val permissions = permissions.toTypedArray()
        with(intent) {
            putExtra(PERMISSION_KEY, permissions)
            putExtra(PERMISSION_REQUEST_CODE_KEY, requestCode)
        }
        activity.startActivityForResult(intent, requestCode)
    }

    override fun afterSettingsActivityResult(
        activity: Activity,
        @StyleRes themeResId: Int
    ) {
        bunchPermissions(activity, themeResId)
    }
}
