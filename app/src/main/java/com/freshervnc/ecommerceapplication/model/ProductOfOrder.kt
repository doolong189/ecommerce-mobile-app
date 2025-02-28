package com.freshervnc.ecommerceapplication.model

data class ProductOfOrder (
    val product : Product,
    val quantity : Int = 0,
)