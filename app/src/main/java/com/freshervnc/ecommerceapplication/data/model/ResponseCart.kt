package com.freshervnc.ecommerceapplication.data.model

data class ResponseCart (
    val products : List<ProductOfCart>?,
    val totalNumber : Int?,
    val totalPrice : Double?,
    val discount : Int?,
)