package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.UserInfo


data class GetAllUserResponse(
    var message : String? = null,
    var users: List<UserInfo>?,
)