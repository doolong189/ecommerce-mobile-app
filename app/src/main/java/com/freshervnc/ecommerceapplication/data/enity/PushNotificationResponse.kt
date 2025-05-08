package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Notification

data class PushNotificationResponse(
    val message : String? = null,
    val notification : Notification? = null,

    )

