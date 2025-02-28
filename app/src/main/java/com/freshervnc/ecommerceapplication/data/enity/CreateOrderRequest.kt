package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.ProductOfCart

@Keep
data class CreateOrderRequest(
    val products: MutableList<ProductOfCart>?,
    val idClient: String? = "",
    val idShipper: String? = ""

)
