package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Notification

@Keep
class GetDetailNotificationResponse (
    val message : String? = null,
    val notification : Notification?
)