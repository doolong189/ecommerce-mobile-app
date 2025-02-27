package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class GetCartResponse (
    val code : String? = null,
    val message : List<String>?,
    val response : Response
)

data class Response(
    val products : List<Product>?,
    val totalNumber : Int?,
    val totalPrice : Double?,
    val discount : Int?
)

data class Product(
    val id : String? = null,
    val name : String? = null,
    val price : Double? = null,
    var quantity : Int? = null,
    val image : String? = null,
    val idStore : String? = null
)