package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.UserInfo

@Keep
data class GetCartRequest(
    val idUser : String? = "",
)
