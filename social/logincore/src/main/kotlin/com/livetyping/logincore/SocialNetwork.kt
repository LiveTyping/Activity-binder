package com.livetyping.logincore

import android.app.Activity
import android.content.Intent


interface SocialNetwork<T> {

    fun login(activity: Activity)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?,
                         successBlock: (result: T) -> Unit,
                         errorBlock: ((error: SocialLoginError) -> Unit)? = null)
}
