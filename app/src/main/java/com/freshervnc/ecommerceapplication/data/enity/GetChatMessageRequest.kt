package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.ProductOfCart

@Keep
data class GetChatMessageRequest(
    val chatId: String? = ""
)
