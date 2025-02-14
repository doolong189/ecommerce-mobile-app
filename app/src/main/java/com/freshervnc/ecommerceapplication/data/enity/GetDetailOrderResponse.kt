package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.Order

@Keep
class GetDetailOrderResponse (
    val message : String? = null,
    val data : Order?
)