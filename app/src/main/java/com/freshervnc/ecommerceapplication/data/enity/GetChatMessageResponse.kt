package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.Chat
import com.freshervnc.ecommerceapplication.model.Message
import com.freshervnc.ecommerceapplication.model.ProductOfCart

@Keep
data class GetChatMessageResponse(
    val message : String? = null,
    val messages : Chat?
)
