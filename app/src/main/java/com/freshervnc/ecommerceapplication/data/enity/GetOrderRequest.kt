package com.freshervnc.ecommerceapplication.data.enity


data class GetOrderRequest(
    val id : String? = "",
    val receiptStatus : Int = 0
)
