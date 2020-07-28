package com.livetyping.activitybinder

import android.app.Application
import com.livetyping.facebook.FacebookInitializer
import com.livetyping.google.GoogleInitializer
import com.livetyping.images.ImagesBinder
import com.livetyping.logincore.SocialLoginBinder
import com.livetyping.permission.PermissionBinder
import com.livetyping.vk.VkInitializer

class BinderExampleApplication : Application() {

    val socialLoginBinder by lazy {
        SocialLoginBinder()
    }

    val permissionBinder by lazy {
        PermissionBinder()
    }

    val testImagesBinder by lazy {
        ImagesBinder(applicationContext.packageName + ".provider", R.xml.file_path)
    }

    override fun onCreate() {
        super.onCreate()
        val socialsInitializers = listOf(
            VkInitializer(),
            FacebookInitializer(),
            GoogleInitializer()
        )
        socialLoginBinder.initializeNetworks(this, socialsInitializers)
    }
}
