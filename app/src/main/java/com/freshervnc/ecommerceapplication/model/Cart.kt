package com.freshervnc.ecommerceapplication.model

data class Cart (
    val id : String = "",
    val products : List<Product>
)