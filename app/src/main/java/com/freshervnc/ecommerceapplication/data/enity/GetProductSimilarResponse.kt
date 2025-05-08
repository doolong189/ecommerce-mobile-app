package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Product

data class GetProductSimilarResponse(
    var message : String? = null,
    var products: List<Product>?,
)