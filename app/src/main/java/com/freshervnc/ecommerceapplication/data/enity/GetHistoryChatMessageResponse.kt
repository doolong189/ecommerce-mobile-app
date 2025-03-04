package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.Message

@Keep
data class GetHistoryChatMessageResponse(
    val message : String? = null,
    val messages : List<Message>?
)
