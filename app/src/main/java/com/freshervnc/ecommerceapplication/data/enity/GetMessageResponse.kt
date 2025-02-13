package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.model.Message

data class GetMessageResponse (
    var messages : List<Message>?
)