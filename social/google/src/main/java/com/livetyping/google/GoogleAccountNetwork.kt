package com.livetyping.google

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.livetyping.logincore.SocialNetwork

class GoogleAccountNetwork(androidClientId: String) : SocialNetwork<GoogleAccountResult> {

    companion object {
        private const val GOOGLE_SIGN_IN_ACTIVITY_REQUEST_CODE = 231
    }

    private val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestServerAuthCode(androidClientId)
        .requestId()
        .requestIdToken(androidClientId)
        .requestEmail()
        .build()

    override fun login(activity: Activity) {
        val signInClient = GoogleSignIn.getClient(activity, signInOptions)
        activity.startActivityForResult(
            signInClient.signInIntent,
            GOOGLE_SIGN_IN_ACTIVITY_REQUEST_CODE
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onSuccess: (result: GoogleAccountResult) -> Unit,
        onFail: (exception: Exception) -> Unit
    ) {
        if (requestCode != GOOGLE_SIGN_IN_ACTIVITY_REQUEST_CODE) return
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val result = GoogleAccountResult(account)
            onSuccess(result)
        } catch (exception: Exception) {
            onFail.invoke(exception)
        }
    }
}
