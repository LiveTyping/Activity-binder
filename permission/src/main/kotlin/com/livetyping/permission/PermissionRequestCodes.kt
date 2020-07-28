package com.livetyping.permission

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build.VERSION_CODES.KITKAT_WATCH

internal object PermissionRequestCodes {

    const val MULTIPLE_PERMISSIONS_CODE = 100

    @SuppressLint("InlinedApi")
    fun getCode(permission: String) =
        when (permission) {
            READ_CALENDAR -> 101
            WRITE_CALENDAR -> 102
            READ_CALL_LOG -> 103
            WRITE_CALL_LOG -> 104
            PROCESS_OUTGOING_CALLS -> 105
            CAMERA -> 106
            READ_CONTACTS -> 107
            WRITE_CONTACTS -> 108
            GET_ACCOUNTS -> 109
            ACCESS_FINE_LOCATION -> 110
            ACCESS_COARSE_LOCATION -> 111
            RECORD_AUDIO -> 112
            READ_PHONE_STATE -> 113
            READ_PHONE_NUMBERS -> 114
            CALL_PHONE -> 115
            ANSWER_PHONE_CALLS -> 116
            ADD_VOICEMAIL -> 117
            USE_SIP -> 118
            BODY_SENSORS -> 119
            SEND_SMS -> 120
            RECEIVE_SMS -> 121
            READ_SMS -> 122
            RECEIVE_WAP_PUSH -> 123
            RECEIVE_MMS -> 124
            READ_EXTERNAL_STORAGE -> 125
            WRITE_EXTERNAL_STORAGE -> 126
            else -> throw IllegalArgumentException("PermissionRequestCodes, undefined permission")
        }
}
