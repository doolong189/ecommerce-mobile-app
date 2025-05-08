package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Product

data class GetProductWithCategoryResponse(
    var message : String? = null,
    var products: List<Product>?,
)