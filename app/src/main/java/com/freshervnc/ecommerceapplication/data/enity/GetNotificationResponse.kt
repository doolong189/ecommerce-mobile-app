package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Notification

@Keep
class GetNotificationResponse (
    val message : String? = null,
    val notifications : List<Notification>?
)