package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.AddNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance

class NotificationRepository {
    suspend fun pushNotification(request : PushNotificationRequest) = RetrofitInstance.api.pushNotification(request)

    suspend fun addNotification(request : AddNotificationRequest) = RetrofitInstance.api.addNotification(request)

    suspend fun getNotification(request: GetNotificationRequest) = RetrofitInstance.api.getNotification(request)

    suspend fun getDetailNotification(request : GetDetailNotificationRequest) = RetrofitInstance.api.getDetailNotification(request)
}