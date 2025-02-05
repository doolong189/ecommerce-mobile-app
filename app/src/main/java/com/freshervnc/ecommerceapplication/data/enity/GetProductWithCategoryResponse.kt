package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.Product
import com.freshervnc.ecommerceapplication.model.UserInfo

@Keep
data class GetProductWithCategoryResponse(
    var message : String? = null,
    var products: List<Product>?,
)