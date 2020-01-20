package com.livetyping.core

import android.app.Activity
import android.content.Intent


abstract class BinderRequest<T> {

    protected var requestCode: Int = 0
    protected lateinit var block: (result: T) -> Unit

    internal fun setRequestCode(requestCode: Int) {
        this.requestCode = requestCode
    }

    internal fun setBlock(block: (result: T) -> Unit) {
        this.block = block
    }

    internal fun internalActivityResult(resultCode: Int, data: Intent?) {
        onActivityResult(resultCode, data)
    }

    internal fun internalPermissionResult(
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(permissions, grantResults)
    }

    internal fun internalRequest(activity: Activity) = request(activity)

    internal fun onActivityResult(resultCode: Int, data: Intent?) {
        //for override
    }

    open fun onRequestPermissionsResult(
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //for override
    }

    protected abstract fun request(activity: Activity)
}