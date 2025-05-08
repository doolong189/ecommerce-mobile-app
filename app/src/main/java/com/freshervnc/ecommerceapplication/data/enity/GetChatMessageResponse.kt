package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Message

data class GetChatMessageResponse(
    val message : String? = null,
    val messages : Message?
)
