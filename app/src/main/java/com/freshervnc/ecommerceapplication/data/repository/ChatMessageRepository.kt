package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetHistoryChatMessageRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class ChatMessageRepository() {

    suspend fun createChatMessage(request : CreateChatMessageRequest) = RetrofitInstance.api.createChatMessage(request)

    suspend fun getChatMessage(request : GetChatMessageRequest) = RetrofitInstance.api.getChatMessage(request)

    suspend fun getHistoryChatMessages(request : GetHistoryChatMessageRequest) = RetrofitInstance.api.getHistoryChatMessages(request)

    // Gửi tin nhắn
    fun sendMessage(socket: io.socket.client.Socket, messageImage: String, senderId: String, message : String, timestamp : Long) {
        val data = JSONObject().apply {
            put("messageImage" , messageImage)
            put("senderId" , senderId)
            put("message" , message)
            put("timestamp" , timestamp)
        }
        socket.emit("message", data)
    }

//    fun emitEvent(socket: Socket, eventName: String, data: JSONObject) {
//        socket.emit(eventName, data)
//    }
//
//    // Lắng nghe sự kiện tin nhắn
//    fun listenForMessages(socket: Socket, listener: Emitter.Listener) {
//        socket.on("message", listener)
//    }
//
//    // Lắng nghe sự kiện lỗi
//    fun listenForError(socket: Socket , listener: Emitter.Listener) {
//        socket.on("error", listener)
//    }

    fun emitEvent(socket: Socket, eventName: String, data: JSONObject) {
        socket.emit(eventName, data)
    }

    fun listenForEvent(socket: Socket, eventName: String, listener: Emitter.Listener) {
        socket.on(eventName, listener)
    }
}