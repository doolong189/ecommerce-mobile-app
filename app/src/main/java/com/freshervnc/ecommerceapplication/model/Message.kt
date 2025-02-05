package com.freshervnc.ecommerceapplication.model

class Message(
    val messageId: String = "",
    var messageText: String = "",
    var receiverId: String = "",
    var senderId: String = "",
    var senderName : String = "",
    var senderAvatar : String = "",
    var messageImageUrl: String = "",
    val timestamp: Long = 0
)