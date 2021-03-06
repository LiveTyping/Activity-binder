package com.livetyping.activitybinder

import android.Manifest
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

    private fun handleButtonMultiplyPermissions() {
        multiply_passive.setOnClickListener {
            permissionBinder.passivePermission(listOf(Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION)) {
                for ((permission, isGranted) in it) {
                    handleOutputResults(isGranted, TAG_MULTIPLY, permission)
                }
            }
        }

        val rationaleText = getString(R.string.active_permission_rationale_text)
        //cab be placed in active permission method as third parameter
        val settingsButtonText = getString(R.string.active_permission_rationale_button_text)
        multiply_active.setOnClickListener {
            permissionBinder.activePermission(listOf(Manifest.permission.SEND_SMS, Manifest.permission.RECORD_AUDIO), rationaleText) {
                for ((permission, isGranted) in it) {
                    handleOutputResults(isGranted, TAG_MULTIPLY, permission)
                }
            }
        }
        multiply_global.setOnClickListener {
            permissionBinder.globalPermission(listOf(Manifest.permission.BODY_SENSORS, Manifest.permission.READ_CALENDAR),
                    ShowGlobalExplanationActivity::class.java) {
                for ((permission, isGranted) in it) {
                    handleOutputResults(isGranted, TAG_MULTIPLY, permission)
                }
            }
        }
    }

    private fun handleButtonSinglePermissions() {
        single_passive.setOnClickListener {
            permissionBinder.passivePermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                handleOutputResults(it, TAG_SINGLE)
            }
        }

        val rationaleText = getString(R.string.active_permission_rationale_text)
        //cab be placed in active permission method as third parameter
        val settingsButtonText = getString(R.string.active_permission_rationale_button_text)
        single_active.setOnClickListener {
            permissionBinder.activePermission(Manifest.permission.CAMERA, rationaleText) {
                handleOutputResults(it, TAG_SINGLE)
            }
        }
        single_global.setOnClickListener {
            permissionBinder.globalPermission(Manifest.permission.USE_SIP,
                    ShowGlobalExplanationActivity::class.java) {
                handleOutputResults(it, TAG_SINGLE)
            }
        }
    }

    private fun handleOutputResults(isGranted: Boolean, tag: String, permission: String = "") {
        if (isGranted) granted(tag, permission) else denied(tag, permission)
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
        permissionBinder.onActivityResult(requestCode, data, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionBinder.onRequestPermissionResult(requestCode, grantResults)
    }

    private fun granted(tag: String, permission: String = "") {
        Log.i(tag, "$permission was granted")
    }

    private fun denied(tag: String, permission: String = "") {
        Log.i(tag, "$permission was denied")
    }
}
