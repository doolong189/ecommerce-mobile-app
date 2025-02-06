package com.freshervnc.ecommerceapplication.model

data class Order (
    val _id : String = "",
    val totalAmount : String = "",
    val date : String = "",
    val receiptStatus : String = "",
    val idClient : UserInfo? = null,
    val idStore : UserInfo? = null,
    val idShipper : UserInfo? = null,
    val products : List<Product>
)