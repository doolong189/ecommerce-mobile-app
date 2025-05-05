package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Product
import com.freshervnc.ecommerceapplication.data.model.UserInfo

@Keep
data class GetProductResponse(
    var message : String? = null,
    var products: List<Product>?,
)