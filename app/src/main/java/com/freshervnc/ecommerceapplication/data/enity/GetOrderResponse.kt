package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Order

data class GetOrderResponse(
    var message : String? = null,
    var data: List<Order>?,
)