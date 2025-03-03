package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class CreateChatMessageRequest(
    val chatId : String? = "",
    val messageImage : String? = "",
    val messageText : String? = "",
    val senderId : String? = "",
    val timestamp : Long? = 0
)
