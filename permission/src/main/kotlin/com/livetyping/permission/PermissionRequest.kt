package com.livetyping.permission

import android.app.Activity
import android.content.Intent
import androidx.annotation.StyleRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

internal abstract class PermissionRequest(
    private val resultListener: (Map<String, Boolean>) -> Unit
) {

    private val permissionMap = mutableMapOf<String, Boolean>()
    protected lateinit var permissions: MutableList<String>
    protected var requestCode = 0

    internal fun needPermissions(
        requestCode: Int,
        permissions: Iterable<String>,
        activity: Activity,
        @StyleRes themeResId: Int
    ) {
        this.requestCode = requestCode
        this.permissions = permissions.toMutableList()
        initPermissionHashMap()
        bunchPermissions(activity, themeResId)
    }

    private fun initPermissionHashMap() {
        permissionMap.clear()
        permissions.forEach { permission ->
            permissionMap[permission] = false
        }
    }

    fun bunchPermissions(activity: Activity, @StyleRes themeResId: Int) {
        if (areAllPermissionsGranted(activity)) {
            syncPermissionsGrantedResult(activity)
            resultListener.invoke(permissionMap)
        } else {
            onPermissionsDenied(activity, themeResId)
        }
    }

    @PermissionChecker.PermissionResult
    protected fun areAllPermissionsGranted(activity: Activity) = permissions.all { permission ->
        isPermissionGranted(activity, permission)
    }

    @PermissionChecker.PermissionResult
    private fun isPermissionGranted(activity: Activity, permission: String) =
        ContextCompat.checkSelfPermission(activity, permission) == PERMISSION_GRANTED

    private fun syncPermissionsGrantedResult(activity: Activity) {
        for ((permission) in permissionMap) {
            permissionMap[permission] = isPermissionGranted(activity, permission)
        }
    }

    internal abstract fun onPermissionsDenied(activity: Activity, @StyleRes themeResId: Int)

    internal abstract fun afterSettingsActivityResult(
        activity: Activity,
        @StyleRes themeResId: Int
    )

    internal abstract fun afterRequest(activity: Activity, @StyleRes themeResId: Int)

    protected fun hasDeniedByUserPermissions(activity: Activity) = permissions.any {
        ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
    }

    protected fun getFirstTimeRequestedPermissions(activity: Activity) = permissions.filter {
        ActivityCompat.shouldShowRequestPermissionRationale(activity, it).not()
    }

    protected fun getDeniedPermissions(activity: Activity) = permissions.filter { isPermissionDenied(it, activity) }

    @PermissionChecker.PermissionResult
    protected fun isPermissionDenied(permission: String, activity: Activity) =
        ContextCompat.checkSelfPermission(activity, permission) == PERMISSION_DENIED

    protected fun invokeResult(activity: Activity) {
        syncPermissionsGrantedResult(activity)
        resultListener.invoke(permissionMap)
    }

    protected fun getPermissionsWithoutRationale(activity: Activity): List<String> {
        return permissions.filter {
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
    }
}
