package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Category

data class GetCategoryResponse(
    var message : String? = null,
    var categorys: List<Category>? = null,
)