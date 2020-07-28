package com.livetyping.activitybinder

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.livetyping.images.ImageRequest
import com.livetyping.images.ImagesBinder
import com.livetyping.images.gallery.GalleryMultipleRequest
import com.livetyping.images.gallery.GallerySingleRequest
import com.livetyping.images.photo.*
import com.livetyping.permission.PermissionBinder
import kotlinx.android.synthetic.main.activity_new_binder.*
import java.io.File


class ImageBinderActivity : AppCompatActivity() {

    private lateinit var imagesBinder: ImagesBinder
    private lateinit var permissionBinder: PermissionBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_binder)
        val binderExampleApplication = application as BinderExampleApplication
        imagesBinder = binderExampleApplication.testImagesBinder
        permissionBinder = binderExampleApplication.permissionBinder

        multiple_gallery.setOnClickListener {
            val request = GalleryMultipleRequest()
            imagesBinder.requestPhoto(request) { files ->
                image.setImageURI(Uri.fromFile(files[0]))
                Toast.makeText(this, files.size.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        single_gallery.setOnClickListener {
            val request = GallerySingleRequest()
            requestAndBindImage(request)
        }

        single_request_chooser.setOnClickListener {
            val request = GallerySingleRequest("select file")
            requestAndBindImage(request)
        }

        default_photo.setOnClickListener {
            permissionBinder.activePermission(
                Manifest.permission.CAMERA,
                getString(R.string.need_camera_permission)
            ) {
                if (it) {
                    val request = PhotoRequestDefaultPath()
                    requestAndBindImage(request)
                }
            }
        }

        cahche_path_photo.setOnClickListener {
            permissionBinder.activePermission(
                Manifest.permission.CAMERA,
                getString(R.string.need_camera_permission)
            ) {
                if (it) {
                    val request = PhotoRequestCachePath("cache_files", maxFileSize = 1000000)
                    requestAndBindImage(request)
                }
            }
        }

        external_cahce_path_photo.setOnClickListener {
            val permissions =
                listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionBinder.activePermission(
                permissions,
                getString(R.string.need_camera_and_external_storage_permission)
            ) {
                if (it.all { it.value }) {
                    val request = PhotoRequestExternalCachePath("external_app_cache_path")
                    requestAndBindImage(request)
                }
            }
        }

        external_files_path_photo.setOnClickListener {
            val permissions =
                listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionBinder.activePermission(
                permissions,
                getString(R.string.need_camera_and_external_storage_permission)
            ) {
                if (it.all { it.value }) {
                    val request = PhotoRequestExternalFilesPath("external_app_files_path")
                    requestAndBindImage(request)
                }
            }
        }

        external_path_photo.setOnClickListener {
            val permissions =
                listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionBinder.activePermission(
                permissions,
                getString(R.string.need_camera_and_external_storage_permission)
            ) {
                if (it.all { it.value }) {
                    val request = PhotoRequestExternalPath("external_files")
                    requestAndBindImage(request)
                }
            }
        }

        files_path_photo.setOnClickListener {
            val permissions =
                listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionBinder.activePermission(
                permissions,
                getString(R.string.need_camera_and_external_storage_permission)
            ) {
                if (it.all { it.value }) {
                    val request = PhotoRequestFilesPath("images")
                    requestAndBindImage(request)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        imagesBinder.attach(this)
        permissionBinder.attach(this)
    }

    override fun onStop() {
        imagesBinder.detach(this)
        permissionBinder.detach(this)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagesBinder.onActivityResult(requestCode, resultCode, data, this)
        permissionBinder.onActivityResult(requestCode, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        imagesBinder.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionBinder.onRequestPermissionResult(requestCode)
    }

    private fun requestAndBindImage(request: ImageRequest<out File>) {
        imagesBinder.requestPhoto(request) { file ->
            image.setImageURI(Uri.fromFile(file))
        }
    }
}
