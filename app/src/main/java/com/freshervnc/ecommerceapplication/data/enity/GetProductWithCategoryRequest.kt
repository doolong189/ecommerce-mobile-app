package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class GetProductWithCategoryRequest(
    val idCategory: String? = "",
    val idUser : String? = ""
)