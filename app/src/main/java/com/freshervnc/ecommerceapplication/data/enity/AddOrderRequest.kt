package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class AddOrderRequest(
    val totalPrice: Int? = 0,
    val date: String? = "",
    val receiptStatus: Int? = 0,
    val idClient: String?,
    val idStore: String?,
    val idShipper: String?,
    val products: MutableList<Product>?
)
