package com.freshervnc.ecommerceapplication.data.enity


class CreateReviewRequest (
    val title: String? = "",
    val date: String? = "",
    val rating: Float? = 0f,
    val idUser : String? = "",
    val idProduct : String? = ""
)