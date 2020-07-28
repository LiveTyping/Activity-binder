package com.livetyping.permission

import android.app.Activity
import android.content.Intent
import androidx.annotation.StyleRes
import com.livetyping.core.Binder
import com.livetyping.permission.PermissionRequestCodes.MULTIPLE_PERMISSIONS_CODE

class PermissionBinder : Binder() {

    private val requests: MutableMap<Int, PermissionRequest> = mutableMapOf()

    fun passivePermission(
        permission: String,
        singleResultListener: (Boolean) -> Unit
    ) {
        val permissionRequest = PassivePermissionRequest { permissionsResults ->
            val isGranted = permissionsResults.getValue(permission)
            singleResultListener(isGranted)
        }
        needPermissions(listOf(permission), permissionRequest)
    }

    @Deprecated(
        "This is old version of the method. Use passivePermissions instead",
        ReplaceWith("passivePermissions(permissions, resultListener)")
    )
    fun passivePermission(
        permissions: Iterable<String>,
        resultListener: (Map<String, Boolean>) -> Unit
    ) = passivePermissions(permissions, resultListener)

    fun passivePermissions(
        permissions: Iterable<String>,
        resultListener: (Map<String, Boolean>) -> Unit
    ) {
        val permissionRequest = PassivePermissionRequest(resultListener)
        needPermissions(permissions, permissionRequest)
    }

    fun activePermission(
        permission: String,
        rationaleText: String,
        @StyleRes themeResId: Int = 0,
        settingsButtonText: String = "settings",
        singleResultListener: (Boolean) -> Unit
    ) {
        val permissionRequest = ActivePermissionRequest(rationaleText, settingsButtonText) { permissionsResults ->
            val isGranted = permissionsResults.getValue(permission)
            singleResultListener(isGranted)
        }
        needPermissions(listOf(permission), permissionRequest, themeResId)
    }

    @Deprecated(
        "This is old version of the method. Use activePermissions instead",
        ReplaceWith("activePermissions(permissions, rationaleText, themeResId, settingsButtonText, resultListener)")
    )
    fun activePermission(
        permissions: Iterable<String>,
        rationaleText: String,
        @StyleRes themeResId: Int = 0,
        settingsButtonText: String = "settings",
        resultListener: (Map<String, Boolean>) -> Unit
    ) = activePermissions(permissions, rationaleText, themeResId, settingsButtonText, resultListener)

    fun activePermissions(
        permissions: Iterable<String>,
        rationaleText: String,
        @StyleRes themeResId: Int = 0,
        settingsButtonText: String = "settings",
        resultListener: (Map<String, Boolean>) -> Unit
    ) {
        val permissionRequest = ActivePermissionRequest(rationaleText, settingsButtonText, resultListener)
        needPermissions(permissions, permissionRequest, themeResId)
    }

    fun globalPermission(
        permission: String,
        preSettingsClass: Class<out PreSettingsActivity>,
        @StyleRes themeResId: Int = 0,
        singleResultListener: (Boolean) -> Unit
    ) {
        val permissionRequest = GlobalPermissionRequest(preSettingsClass) { permissionsResults ->
            val isGranted = permissionsResults.getValue(permission)
            singleResultListener(isGranted)
        }
        needPermissions(listOf(permission), permissionRequest, themeResId)
    }

    @Deprecated(
        "This is old version of the method. Use globalPermissions instead",
        ReplaceWith("globalPermissions(permissions, preSettingsClass, themeResId, resultListener)")
    )
    fun globalPermission(
        permissions: Iterable<String>,
        preSettingsClass: Class<out PreSettingsActivity>,
        @StyleRes themeResId: Int = 0,
        resultListener: (Map<String, Boolean>) -> Unit
    ) = globalPermissions(permissions, preSettingsClass, themeResId, resultListener)

    fun globalPermissions(
        permissions: Iterable<String>,
        preSettingsClass: Class<out PreSettingsActivity>,
        @StyleRes themeResId: Int = 0,
        resultListener: (Map<String, Boolean>) -> Unit
    ) {
        val permissionRequest = GlobalPermissionRequest(preSettingsClass, resultListener)
        needPermissions(permissions, permissionRequest, themeResId)
    }

    fun onRequestPermissionResult(code: Int, @StyleRes themeResId: Int = 0) {
        val requester = requests[code]
        val attachedObject = getAttachedObject()
            ?: throw IllegalStateException("PermissionRepository. Haven't attached activity")
        requester?.afterRequest(attachedObject, themeResId)
    }

    fun onActivityResult(
        requestCode: Int,
        activity: Activity,
        @StyleRes themeResId: Int = 0
    ) {
        requests[requestCode]?.afterSettingsActivityResult(activity, themeResId)
    }

    private fun needPermissions(
        permissions: Iterable<String>,
        request: PermissionRequest,
        @StyleRes themeResId: Int = 0
    ) {
        val attachedObject = getAttachedObject()
            ?: throw IllegalStateException("PermissionRepository. Haven't attached activity")
        val requestCode = calculateRequestCode(permissions)
        requests[requestCode] = request
        request.needPermissions(requestCode, permissions, attachedObject, themeResId)
    }

    private fun calculateRequestCode(permissions: Iterable<String>) =
        if (permissions.count() == 1) {
            PermissionRequestCodes.getCode(permissions.first())
        } else {
            MULTIPLE_PERMISSIONS_CODE
        }
}
