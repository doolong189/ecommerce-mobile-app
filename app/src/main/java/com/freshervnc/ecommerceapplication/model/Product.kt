package com.freshervnc.ecommerceapplication.model

data class Product(
    val _id: String ,
    val name: String ,
    val price: Double ,
    val quantity: Int ,
    val description: String ,
    val image: String,
    val idUser: UserInfo?,
    val idCategory: Category?,
)