package com.freshervnc.ecommerceapplication.data.enity


data class LoginRequest(
    val email: String? = "",
    val password : String? = ""
)