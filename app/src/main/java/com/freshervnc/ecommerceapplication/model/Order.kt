package com.freshervnc.ecommerceapplication.model


data class Order (
    val _id : String = "",
    val totalPrice : Double = 0.0,
    val date : String = "",
    val receiptStatus : Int = 0,
    val idClient : UserInfo? = null,
    val idShipper : UserInfo? = null,
    val products : List<Product>
)