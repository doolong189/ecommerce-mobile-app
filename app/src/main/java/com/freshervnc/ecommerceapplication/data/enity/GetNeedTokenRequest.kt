package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class GetNeedTokenRequest (
    val id : String = "",
    val token : String = ""
)