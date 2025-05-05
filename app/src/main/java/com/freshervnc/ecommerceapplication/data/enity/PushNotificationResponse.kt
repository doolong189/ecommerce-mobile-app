package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Notification

@Keep
data class PushNotificationResponse(
    val message : String? = null,
    val notification : Notification? = null,

    )

