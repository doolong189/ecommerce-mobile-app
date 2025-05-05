package com.freshervnc.ecommerceapplication.utils

import android.content.Context
import android.util.Log
import com.freshervnc.ecommerceapplication.data.model.Chat
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class SocketIOManager() {
    private var socket: Socket? = null
    fun connect() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket = IO.socket("${Constants.BASE_URL_SOCKET}")
                socket?.connect()
                Log.e(Constants.TAG, "socket connect")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: String) {
        socket?.emit("message",message)
        Log.e(Constants.TAG,"send message")
    }

    fun receiveMessage(callback: (String?) -> Unit) : Chat {
        var mChat = Chat()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket?.on("message") { args ->
                    val data = args[0] as JSONObject
                    try {
                        val username = data.getString("id")
                        val message = data.getString("message")
                        mChat = Chat(
                            messageImage = "",
                            messageText = message,
                            senderId = username,
                            timestamp = System.currentTimeMillis())
                        Log.e(Constants.TAG,"sendmessage")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
        return mChat
    }

    fun close() {
        CoroutineScope(Dispatchers.IO).launch {
            socket?.close()
            socket = null
        }
    }

    fun isConnected(): Boolean {
        return socket?.isActive ?: false
    }
}