package com.livetyping.google

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.livetyping.logincore.SocialNetwork
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN as DEFAULT_SIGN_IN

class GoogleTokenNetwork(androidClientId: String) : SocialNetwork<GoogleTokenResult> {

    companion object {
        private const val GOOGLE_SIGN_IN_ACTIVITY_REQUEST_CODE = 231
        private const val HANDLER_THREAD_NAME = "GoogleTokenNetworkHandlerThread"
        private const val SCOPES = "oauth2:profile email"
    }

    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null

    private lateinit var googleAccount: GoogleSignInAccount
    private lateinit var successBlock: (result: GoogleTokenResult) -> Unit

    private var context: Context? = null

    private val signInOptions = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
        .requestServerAuthCode(androidClientId)
        .requestId()
        .requestIdToken(androidClientId)
        .requestEmail()
        .build()

    override fun login(activity: Activity) {
        val signInClient = GoogleSignIn.getClient(activity, signInOptions)
        context = activity.applicationContext
        activity.startActivityForResult(
            signInClient.signInIntent,
            GOOGLE_SIGN_IN_ACTIVITY_REQUEST_CODE
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onSuccess: (result: GoogleTokenResult) -> Unit,
        onFail: (exception: Exception) -> Unit
    ) {
        if (requestCode != GOOGLE_SIGN_IN_ACTIVITY_REQUEST_CODE) return
        this.successBlock = onSuccess
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            getToken(task)
        } catch (exception: Exception) {
            onFail.invoke(exception)
            handler = null
            handlerThread = null
            context = null
        }
    }

    private fun getToken(task: Task<GoogleSignInAccount>) {
        checkNotNull(context) { "Context is null!" }
        googleAccount = task.getResult(ApiException::class.java)!!
        handlerThread = HandlerThread(HANDLER_THREAD_NAME)
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
        handler!!.post(loadToken)
    }

    private val loadToken: () -> Unit = {
        val account = googleAccount.account
        val token = GoogleAuthUtil.getToken(context, account, SCOPES)
        val result = GoogleTokenResult(token)
        successBlock.invoke(result)
        handler!!.removeMessages(0)
        handler = null
        handlerThread = null
        context = null
    }
}
