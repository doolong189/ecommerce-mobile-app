package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class CreateOrderRequest(
    val products: MutableList<Product>?,
    val idClient: String? = "",
    val idShipper: String? = ""

)
