package com.freshervnc.ecommerceapplication.model

data class Category(
    val _id : String = "",
    val name : String = "",
    val image: String = ""
){
    override fun toString(): String {
        return name
    }
}