package com.freshervnc.ecommerceapplication.data.model

data class Chat(
    var messageImage: String = "",
    var messageText: String = "",
    var senderId: String = "",
    val timestamp: Long = 0
)