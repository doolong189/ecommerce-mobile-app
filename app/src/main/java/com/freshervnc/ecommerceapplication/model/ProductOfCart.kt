package com.freshervnc.ecommerceapplication.model

data class ProductOfCart (
    val id : String? = null,
    val name : String? = null,
    val price : Double? = null,
    var quantity : Int? = null,
    val discount : Int,
    val image : List<String>? = null,
    val idStore : String? = null
)