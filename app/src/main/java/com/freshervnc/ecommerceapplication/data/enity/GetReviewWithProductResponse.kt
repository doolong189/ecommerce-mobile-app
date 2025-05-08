package com.freshervnc.ecommerceapplication.data.enity

import com.freshervnc.ecommerceapplication.data.model.Review

data class GetReviewWithProductResponse(
    var message : String? = null,
    var review: List<Review>?,
)