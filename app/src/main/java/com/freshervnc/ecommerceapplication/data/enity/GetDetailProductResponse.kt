package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Product
import com.freshervnc.ecommerceapplication.data.model.UserInfo

@Keep
data class GetDetailProductResponse(
    var message : String? = null,
    var data: Product?,
)