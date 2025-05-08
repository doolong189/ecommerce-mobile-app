package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Notification

class GetNotificationResponse (
    val message : String? = null,
    val notifications : List<Notification>?
)