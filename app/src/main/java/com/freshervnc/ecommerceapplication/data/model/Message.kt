package com.freshervnc.ecommerceapplication.data.model

class Message (
    val messageId : String,
    val senderId : UserInfo,
    val receiverId : UserInfo,
    val chats : List<Chat>,
    val lastMsg : String,
    val lastMsgTime : Long
)