package com.livetyping.google

import com.livetyping.logincore.SocialLoginResult

data class GoogleTokenResult(
    val accessToken: String
) : SocialLoginResult
