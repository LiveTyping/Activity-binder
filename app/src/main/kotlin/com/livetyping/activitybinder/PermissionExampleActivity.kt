package com.livetyping.activitybinder

import android.Manifest.permission.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.livetyping.permission.PermissionBinder
import kotlinx.android.synthetic.main.activity_permissions.*

class PermissionExampleActivity : AppCompatActivity() {

    companion object {
        private const val TAG_SINGLE = "single"
        private const val TAG_MULTIPLY = "multiply"
    }

    private lateinit var permissionBinder: PermissionBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)
        permissionBinder = (application as BinderExampleApplication).permissionBinder
        handleButtonSinglePermissions()
        handleButtonMultiplyPermissions()
    }

    private fun handleButtonSinglePermissions() {
        setOnSinglePassivePermissionClickListener()
        setOnSingleActivePermissionClickListener()
        setOnSingleGlobalPermissionClickListener()
    }

    private fun setOnSinglePassivePermissionClickListener() {
        single_passive.setOnClickListener {
            permissionBinder.passivePermission(READ_EXTERNAL_STORAGE) { isGranted -> onPermissionResult(isGranted) }
        }
    }

    private fun onPermissionResult(isGranted: Boolean) {
        handleOutputResults(isGranted, TAG_SINGLE)
    }

    private fun handleOutputResults(
        isGranted: Boolean,
        tag: String,
        permission: String = ""
    ) {
        if (isGranted) granted(tag, permission) else denied(tag, permission)
    }

    private fun granted(tag: String, permission: String = "") {
        Log.i(tag, "$permission was granted")
    }

    private fun denied(tag: String, permission: String = "") {
        Log.i(tag, "$permission was denied")
    }

    private fun setOnSingleActivePermissionClickListener() {
        val rationaleText = getString(R.string.active_permission_rationale_text)
        val dialogCustomThemeResId = R.style.SingleActivePermissionDialogTheme
        val settingsButtonTitle = getString(R.string.active_permission_rationale_button_text)
        single_active.setOnClickListener {
            permissionBinder.activePermission(
                CAMERA,
                rationaleText,
                dialogCustomThemeResId,
                settingsButtonTitle
            ) { isGranted -> onPermissionResult(isGranted) }
        }
    }

    private fun setOnSingleGlobalPermissionClickListener() {
        single_global.setOnClickListener {
            permissionBinder.globalPermission(
                USE_SIP,
                ShowGlobalExplanationActivity::class.java
            ) { isGranted -> onPermissionResult(isGranted) }
        }
    }

    private fun handleButtonMultiplyPermissions() {
        setOnMultiplePassivePermissionClickListener()
        setOnMultipleActivePermissionClickListener()
        setOnMultipleGlobalPermissionClickListener()
    }

    private fun setOnMultiplePassivePermissionClickListener() {
        val passivePermissions = listOf(READ_CONTACTS, ACCESS_FINE_LOCATION)
        multiply_passive.setOnClickListener {
            permissionBinder.passivePermissions(passivePermissions) { results -> onPermissionsResults(results) }
        }
    }

    private fun onPermissionsResults(permissionsResults: Map<String, Boolean>) {
        for ((permission, isGranted) in permissionsResults) {
            handleOutputResults(isGranted, TAG_MULTIPLY, permission)
        }
    }

    private fun setOnMultipleActivePermissionClickListener() {
        val activePermissions = listOf(RECORD_AUDIO, CAMERA)
        val rationaleText = getString(R.string.active_permission_rationale_text)
        multiply_active.setOnClickListener {
            permissionBinder.activePermissions(
                activePermissions,
                rationaleText
            ) { results -> onPermissionsResults(results) }
        }
    }

    private fun setOnMultipleGlobalPermissionClickListener() {
        val globalPermissions = listOf(READ_PHONE_STATE, READ_CALENDAR)
        multiply_global.setOnClickListener {
            permissionBinder.globalPermissions(
                globalPermissions,
                ShowGlobalExplanationActivity::class.java
            ) { results -> onPermissionsResults(results) }
        }
    }


    override fun onStart() {
        super.onStart()
        permissionBinder.attach(this)
    }

    override fun onStop() {
        permissionBinder.detach(this)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionBinder.onActivityResult(requestCode, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionBinder.onRequestPermissionResult(requestCode)
    }
}
