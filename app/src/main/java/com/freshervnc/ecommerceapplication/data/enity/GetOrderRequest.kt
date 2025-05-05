package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.UserInfo

@Keep
data class GetOrderRequest(
    val id : String? = "",
    val receiptStatus : Int = 0
)
