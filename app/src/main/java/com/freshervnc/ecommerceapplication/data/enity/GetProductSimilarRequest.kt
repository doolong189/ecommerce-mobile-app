package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class GetProductSimilarRequest (
    val idUser : String = "",
    val idProduct : String = ""
)