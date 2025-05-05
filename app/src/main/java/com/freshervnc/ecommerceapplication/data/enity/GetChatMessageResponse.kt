package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Message

@Keep
data class GetChatMessageResponse(
    val message : String? = null,
    val messages : Message?
)
