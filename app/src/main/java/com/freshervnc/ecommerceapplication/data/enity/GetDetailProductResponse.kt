package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Product

data class GetDetailProductResponse(
    var message : String? = null,
    var data: Product?,
)