package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Message

data class GetHistoryChatMessageResponse(
    val message : String? = null,
    val messages : List<Message>?
)
