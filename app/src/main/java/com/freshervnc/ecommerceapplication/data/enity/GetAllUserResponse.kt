package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.UserInfo

@Keep
data class GetAllUserResponse(
    var message : String? = null,
    var users: List<UserInfo>?,
)