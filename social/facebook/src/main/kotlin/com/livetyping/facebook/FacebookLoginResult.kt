package com.livetyping.facebook

import com.facebook.login.LoginResult
import com.livetyping.logincore.SocialLoginResult

data class FacebookLoginResult(
    val accessToken: String,
    val applicationId: String,
    val userId: String
) : SocialLoginResult

internal fun LoginResult.toFacebookLoginResult() = with(accessToken) {
    FacebookLoginResult(token, applicationId, userId)
}
