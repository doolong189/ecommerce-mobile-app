package com.freshervnc.ecommerceapplication.data.enity


data class PushNotificationRequest(
    val registrationToken : String? = "",
    val title : String? = "",
    val body : String? = "",
    val image : String? = "",
    val type : String? = ""
)
