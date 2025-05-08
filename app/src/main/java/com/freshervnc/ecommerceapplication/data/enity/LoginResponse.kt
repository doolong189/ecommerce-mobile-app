package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.UserInfo

data class LoginResponse (
    var message : String? = null,
    var user : UserInfo?
)