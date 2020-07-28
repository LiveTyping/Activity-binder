package com.livetyping.facebook

import android.app.Activity
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.livetyping.logincore.SocialNetwork

class FacebookNetwork : SocialNetwork<FacebookLoginResult> {

    companion object {
        private const val PUBLIC_PROFILE_PERMISSION = "public_profile"
        private const val EMAIL_PERMISSION = "email"
    }

    private val callbackManager = CallbackManager.Factory.create()

    override fun login(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            listOf(PUBLIC_PROFILE_PERMISSION, EMAIL_PERMISSION)
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onSuccess: (result: FacebookLoginResult) -> Unit,
        onFail: (exception: Exception) -> Unit
    ) {
        LoginManager.getInstance().registerCallback(
            callbackManager,
            facebookCallback(onSuccess, onFail)
        )
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun facebookCallback(
        onSuccess: (result: FacebookLoginResult) -> Unit,
        onFail: (exception: Exception) -> Unit
    ) = object : FacebookCallback<LoginResult> {

        override fun onSuccess(loginResult: LoginResult) {
            val result = loginResult.toFacebookLoginResult()
            onSuccess.invoke(result)
        }

        override fun onCancel() {
            LoginManager.getInstance().unregisterCallback(callbackManager)
        }

        override fun onError(exception: FacebookException) {
            onFail.invoke(exception)
        }
    }
}
