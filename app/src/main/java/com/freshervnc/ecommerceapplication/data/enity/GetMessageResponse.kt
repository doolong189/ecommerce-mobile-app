package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Chat

data class GetMessageResponse (
    var messages : List<Chat>?
)