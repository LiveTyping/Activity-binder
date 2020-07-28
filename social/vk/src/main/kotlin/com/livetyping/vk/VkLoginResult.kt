package com.livetyping.vk

import com.livetyping.logincore.SocialLoginResult
import com.vk.api.sdk.auth.VKAccessToken

data class VkLoginResult(
    val accessToken: String,
    val email: String?
) : SocialLoginResult

internal fun VKAccessToken.toVkLoginResult() = VkLoginResult(accessToken, email)
