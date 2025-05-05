package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.ProductOfCart

@Keep
data class GetChatMessageRequest(
    val messageId: String? = ""
)
