package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetHistoryChatMessageRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance

class ChatMessageRepository {

    suspend fun createChatMessage(request : CreateChatMessageRequest) = RetrofitInstance.api.createChatMessage(request)

    suspend fun getChatMessage(request : GetChatMessageRequest) = RetrofitInstance.api.getChatMessage(request)

    suspend fun getHistoryChatMessages(request : GetHistoryChatMessageRequest) = RetrofitInstance.api.getHistoryChatMessages(request)
}