package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class CreateNotificationRequest(
    val title : String? = "",
    val body : String? = "",
    val image : String? = "",
    val idUser : String? = "",
    val type : String? = ""
)
