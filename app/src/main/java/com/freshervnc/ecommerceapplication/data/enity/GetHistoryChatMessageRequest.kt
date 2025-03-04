package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.ProductOfCart

@Keep
data class GetHistoryChatMessageRequest(
    val senderId: String? = ""
)
