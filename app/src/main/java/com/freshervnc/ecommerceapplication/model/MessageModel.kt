package com.freshervnc.ecommerceapplication.model

class MessageModel(
    val messageId: String = "",
    var messageText: String = "",
    var senderId: String = "",
    var messageImageUrl: String = "",
    val timestamp: Long = 0
)