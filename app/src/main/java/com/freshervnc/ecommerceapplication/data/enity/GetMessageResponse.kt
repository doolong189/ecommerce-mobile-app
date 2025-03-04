package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.model.Chat

data class GetMessageResponse (
    var messages : List<Chat>?
)