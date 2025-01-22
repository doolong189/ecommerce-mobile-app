package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.UserInfo

@Keep
data class GetMessageRequest(
    val users : List<UserInfo>? = null,
    val senderId : String? = ""
)