package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Product
import com.freshervnc.ecommerceapplication.data.model.ProductOfCart
import com.freshervnc.ecommerceapplication.data.model.UserInfo

@Keep
class CreateReviewRequest (
    val title: String? = "",
    val date: String? = "",
    val rating: Float? = 0f,
    val idUser : String? = "",
    val idProduct : String? = ""
)