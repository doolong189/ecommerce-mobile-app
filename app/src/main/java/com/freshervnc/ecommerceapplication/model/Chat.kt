package com.freshervnc.ecommerceapplication.model

import com.google.android.datatransport.cct.StringMerger

class Chat (
    val chatId : String,
    val messages : List<Message>,
    val lastMsg : String,
    val lastMsgTime : Long
)