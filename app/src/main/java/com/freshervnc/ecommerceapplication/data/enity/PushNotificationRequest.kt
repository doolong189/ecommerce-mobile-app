package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class PushNotificationRequest(
    val registrationToken : String? = "",
    val title : String? = "",
    val body : String? = "",
    val image : String? = "",
    val type : String? = ""
)
