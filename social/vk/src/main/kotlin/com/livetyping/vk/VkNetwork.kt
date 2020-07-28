package com.livetyping.vk

import android.app.Activity
import android.content.Intent
import com.livetyping.logincore.SocialNetwork
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.*
import com.vk.api.sdk.auth.VKAuthCallback.Companion.AUTH_CANCELED
import com.vk.api.sdk.auth.VKScope.*

class VkNetwork : SocialNetwork<VkLoginResult> {

    companion object {
        private const val VK_AUTH_CANCELLED = "VK SDK: authorization canceled"
        private const val VK_UNKNOWN_ERROR = "VK SDK: Unknown error"
    }

    override fun login(activity: Activity) {
        VK.login(activity, listOf(EMAIL))
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onSuccess: (result: VkLoginResult) -> Unit,
        onFail: (exception: Exception) -> Unit
    ) {
        VK.onActivityResult(
            requestCode,
            resultCode,
            data,
            vkAuthCallback(onSuccess, onFail)
        )
    }

    private fun vkAuthCallback(
        onSuccess: (result: VkLoginResult) -> Unit,
        onFail: (exception: Exception) -> Unit
    ) = object : VKAuthCallback {

        override fun onLogin(token: VKAccessToken) {
            val result = token.toVkLoginResult()
            onSuccess.invoke(result)
        }

        override fun onLoginFailed(errorCode: Int) {
            val errorMessage = if (errorCode == AUTH_CANCELED) VK_AUTH_CANCELLED else VK_UNKNOWN_ERROR
            val exception = Exception(errorMessage)
            onFail.invoke(exception)
        }
    }
}
