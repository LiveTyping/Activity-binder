package com.livetyping.activitybinder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.GoogleAuthUtil
import com.livetyping.facebook.FacebookLoginResult
import com.livetyping.facebook.FacebookNetwork
import com.livetyping.google.GoogleAccountNetwork
import com.livetyping.google.GoogleAccountResult
import com.livetyping.google.GoogleTokenNetwork
import com.livetyping.google.GoogleTokenResult
import com.livetyping.logincore.SocialLoginBinder
import com.livetyping.logincore.SocialLoginResult
import com.livetyping.logincore.SocialNetwork
import com.livetyping.vk.VkLoginResult
import com.livetyping.vk.VkNetwork
import kotlinx.android.synthetic.main.activity_social.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SocialActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        private const val SCOPES = "oauth2:profile email"
        private const val GOOGLE_ANDROID_CLIENT_ID =
            "962786784406-vrmqfde2mtng3vei55djkqehd5me9t42.apps.googleusercontent.com"
    }

    private lateinit var socialLoginBinder: SocialLoginBinder

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        // can be injected with dagger
        socialLoginBinder = (application as BinderExampleApplication).socialLoginBinder

        initVkLogin()
        initFacebookLogin()
        initGoogleAccountLogin()
        initGoogleTokenLogin()
    }

    private fun initVkLogin() {
        val socialNetwork = VkNetwork()
        login_vk.setOnClickListener {
            loginWith(socialNetwork, ::onSuccessVkLogin)
        }
    }

    private fun <T : SocialLoginResult> loginWith(
        socialNetwork: SocialNetwork<T>,
        onSuccess: (result: T) -> Unit
    ) = socialLoginBinder.loginWith(socialNetwork, onSuccess, ::onFail)

    private fun initFacebookLogin() {
        val socialNetwork = FacebookNetwork()
        login_fb.setOnClickListener {
            loginWith(socialNetwork, ::onSuccessFacebookLogin)
        }
    }

    private fun initGoogleAccountLogin() {
        val socialNetwork = GoogleAccountNetwork(GOOGLE_ANDROID_CLIENT_ID)
        login_google.setOnClickListener {
            loginWith(socialNetwork, ::onSuccessGoogleAccountLogin)
        }
    }

    private fun initGoogleTokenLogin() {
        val socialNetwork = GoogleTokenNetwork(GOOGLE_ANDROID_CLIENT_ID)
        login_google_token.setOnClickListener {
            loginWith(socialNetwork, ::onSuccessGoogleTokenLogin)
        }
    }

    private fun onSuccessVkLogin(result: VkLoginResult) {
        val token = result.accessToken
        val email = result.email
        showToast("$email\n$token")
    }

    private fun showToast(message: String) {
        Toast.makeText(this@SocialActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun onSuccessFacebookLogin(result: FacebookLoginResult) {
        val token = result.accessToken
        val appId = result.applicationId
        val userId = result.userId
        showToast("$userId\n$appId\n$token")
    }

    private fun onSuccessGoogleAccountLogin(result: GoogleAccountResult) {
        launch {
            val account = result.googleAccount.account
            val token = withContext(Dispatchers.IO) {
                GoogleAuthUtil.getToken(this@SocialActivity, account, SCOPES)
            }
            showToast(token)
        }
    }

    private fun onSuccessGoogleTokenLogin(result: GoogleTokenResult) {
        val token = result.accessToken
        showToast(token)
    }

    private fun onFail(exception: Exception) {
        showToast(exception.localizedMessage)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        socialLoginBinder.attach(this)
    }

    override fun onStop() {
        socialLoginBinder.detach(this)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        socialLoginBinder.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
