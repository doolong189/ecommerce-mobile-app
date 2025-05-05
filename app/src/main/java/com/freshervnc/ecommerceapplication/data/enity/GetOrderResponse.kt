package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Order
import com.freshervnc.ecommerceapplication.data.model.Product
import com.freshervnc.ecommerceapplication.data.model.UserInfo

@Keep
data class GetOrderResponse(
    var message : String? = null,
    var data: List<Order>?,
)