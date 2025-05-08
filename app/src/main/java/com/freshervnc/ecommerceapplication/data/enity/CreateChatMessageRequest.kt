package com.freshervnc.ecommerceapplication.data.enity

data class CreateChatMessageRequest(
    val messageId : String? = "",
    val messageImage : String? = "",
    val messageText : String? = "",
    val senderId : String? = "",
    val receiverId : String? = "",
    val senderChatId : String? = "",
    val timestamp : Long? = 0
)
