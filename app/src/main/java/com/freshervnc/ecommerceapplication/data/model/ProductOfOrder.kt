package com.freshervnc.ecommerceapplication.data.model

data class ProductOfOrder (
    val product : Product,
    val quantity : Int = 0,
)