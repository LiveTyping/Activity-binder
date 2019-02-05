package com.livetyping.permission

import android.app.Activity
import android.content.Intent
import android.support.annotation.StringRes
import android.support.v4.content.PermissionChecker
import com.livetyping.core.Binder


class PermissionBinder : Binder() {
    private val requests: MutableMap<Int, PermissionRequest> = mutableMapOf()
    private val permissionCodes: PermissionRequestCodes = PermissionRequestCodes()

    fun passivePermission(permission: String, resultListener: (result: Boolean) -> Unit) {
        needPermission(permission, PassivePermissionRequest(resultListener))
    }

    fun activePermission(permission: String,
                         rationaleText: String,
                         @StringRes settingsButtonText: String = "settings",
                         resultListener: (result: Boolean) -> Unit) {
        needPermission(permission, ActivePermissionRequest(resultListener, settingsButtonText, rationaleText))
    }

    fun globalPermission(permission: String, clazz: Class<out PreSettingsActivity>, resultListener: (result: Boolean) -> Unit) {
        needPermission(permission, GlobalPermissionRequest(clazz, resultListener))
    }

    fun onRequestPermissionResult(code: Int, grantResults: IntArray) {
        val granted = grantResults.isNotEmpty() && grantResults[0] == PermissionChecker.PERMISSION_GRANTED
        val requester = requests[code]
        getAttachedObject()
                ?: throw IllegalStateException("PermissionRepository. Haven't attached activity")
        requester?.afterRequest(granted, getAttachedObject()!!)
    }

    fun onActivityResult(requestCode: Int, data: Intent?, activity: Activity) {
        requests[requestCode]?.afterSettingsActivityResult(requestCode, data, activity)
    }


    private fun needPermission(permission: String, request: PermissionRequest) {
        getAttachedObject()
                ?: throw IllegalStateException("PermissionRepository. Haven't attached activity")
        val requestCode = permissionCodes.getCode(permission)
        requests.put(requestCode, request)
        requests[requestCode]!!.needPermission(requestCode, permission, getAttachedObject()!!)
    }

}
