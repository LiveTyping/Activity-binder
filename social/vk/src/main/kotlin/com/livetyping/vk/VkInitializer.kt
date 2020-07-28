package com.livetyping.vk

import android.app.Application
import com.livetyping.logincore.SocialInitializer
import com.vk.api.sdk.VK

class VkInitializer : SocialInitializer {

    override fun init(app: Application) {
        VK.initialize(app)
    }
}
