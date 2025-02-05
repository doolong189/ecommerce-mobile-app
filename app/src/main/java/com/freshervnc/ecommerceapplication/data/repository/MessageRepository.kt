package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance
import com.freshervnc.ecommerceapplication.model.UserInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MessageRepository {

    private val database = FirebaseDatabase.getInstance()

    companion object {
        private var INSTANCE: MessageRepository? = null

        fun getInstance(): MessageRepository = INSTANCE ?: MessageRepository().apply {
            INSTANCE = this
        }
    }

    fun getHistoryMessage(senderRoom : String) : DatabaseReference =
        database.reference.child("Chats").child(senderRoom)

}