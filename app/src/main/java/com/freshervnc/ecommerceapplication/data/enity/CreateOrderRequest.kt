package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.ProductOfCart

data class CreateOrderRequest(
    val products: MutableList<ProductOfCart>?,
    val idClient: String? = "",
    val idShipper: String? = ""

)
