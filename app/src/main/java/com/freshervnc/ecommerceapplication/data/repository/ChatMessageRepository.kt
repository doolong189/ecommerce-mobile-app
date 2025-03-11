package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetHistoryChatMessageRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance
import org.json.JSONObject
import io.socket.client.Socket
import io.socket.emitter.Emitter
class ChatMessageRepository(private val socket: Socket) {

    suspend fun createChatMessage(request : CreateChatMessageRequest) = RetrofitInstance.api.createChatMessage(request)

    suspend fun getChatMessage(request : GetChatMessageRequest) = RetrofitInstance.api.getChatMessage(request)

    suspend fun getHistoryChatMessages(request : GetHistoryChatMessageRequest) = RetrofitInstance.api.getHistoryChatMessages(request)

    // Gửi tin nhắn
    fun sendMessage(messageImage: String, senderId: String , message : String, timestamp : Long) {
        val data = JSONObject().apply {
            put("messageImage" , messageImage)
            put("senderId" , senderId)
            put("message" , message)
            put("timestamp" , timestamp)
        }
        socket.emit("message", data)
    }

    // Lắng nghe sự kiện tin nhắn
    fun listenForMessages(listener: Emitter.Listener) {
        socket.on("message", listener)
    }

    // Lắng nghe sự kiện lỗi
    fun listenForError(listener: Emitter.Listener) {
        socket.on("error", listener)
    }
}