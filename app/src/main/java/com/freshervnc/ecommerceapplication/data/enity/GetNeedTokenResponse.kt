package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.model.UserInfo

data class GetNeedTokenResponse (
    var message : String? = null,
    val data : UserInfo?
)