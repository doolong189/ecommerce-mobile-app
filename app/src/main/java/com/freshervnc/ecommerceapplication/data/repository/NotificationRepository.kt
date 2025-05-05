package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.CreateNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance

class NotificationRepository {
    suspend fun pushNotification(request : PushNotificationRequest) = RetrofitInstance.apiService.pushNotification(request)

    suspend fun createNotification(request : CreateNotificationRequest) = RetrofitInstance.apiService.createNotification(request)

    suspend fun getNotification(request: GetNotificationRequest) = RetrofitInstance.apiService.getNotification(request)

    suspend fun getDetailNotification(request : GetDetailNotificationRequest) = RetrofitInstance.apiService.getDetailNotification(request)
}