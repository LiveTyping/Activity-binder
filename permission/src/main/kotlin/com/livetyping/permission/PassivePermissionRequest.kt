package com.livetyping.permission

import android.app.Activity
import android.content.Intent
import androidx.annotation.StyleRes
import androidx.core.app.ActivityCompat

internal class PassivePermissionRequest(
    resultListener: (permissionMap: Map<String, Boolean>) -> Unit
) : PermissionRequest(resultListener) {

    override fun onPermissionsDenied(activity: Activity, @StyleRes themeResId: Int) {
        val permissionsWithoutRationale = getFirstTimeRequestedPermissions(activity)
        val hasFirstTimeRequestedPermissions = permissionsWithoutRationale.isNotEmpty()
        if (hasFirstTimeRequestedPermissions) {
            val permissions = permissionsWithoutRationale.toTypedArray()
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        } else {
            invokeResult(activity)
        }
    }

    override fun afterRequest(activity: Activity, @StyleRes themeResId: Int) {
        invokeResult(activity)
    }

    override fun afterSettingsActivityResult(
        activity: Activity,
        @StyleRes themeResId: Int
    ) {
        //nothing for this request
    }
}
