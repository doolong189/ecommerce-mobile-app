package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.Review
import com.freshervnc.ecommerceapplication.model.UserInfo

@Keep
data class GetReviewWithProductResponse(
    var message : String? = null,
    var review: List<Review>?,
)