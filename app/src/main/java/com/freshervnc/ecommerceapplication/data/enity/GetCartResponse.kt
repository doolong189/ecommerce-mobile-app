package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.ResponseCart


data class GetCartResponse (
    val code : String? = null,
    val message : List<String>?,
    val response : ResponseCart
)
