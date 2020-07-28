package com.livetyping.google

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.livetyping.logincore.SocialLoginResult

data class GoogleAccountResult(
    val googleAccount: GoogleSignInAccount
) : SocialLoginResult
