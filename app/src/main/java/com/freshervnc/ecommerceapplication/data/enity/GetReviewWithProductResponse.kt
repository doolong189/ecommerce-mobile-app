package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.Review
import com.freshervnc.ecommerceapplication.data.model.UserInfo

@Keep
data class GetReviewWithProductResponse(
    var message : String? = null,
    var review: List<Review>?,
)