package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.Category

@Keep
data class GetCategoryResponse(
    var message : String? = null,
    var categorys: List<Category>? = null,
)