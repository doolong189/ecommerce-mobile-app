package com.freshervnc.ecommerceapplication.utils

object Extensions{
    fun urlImageFb(str : String) : String{
        val url = "https://graph.facebook.com/${str}/picture?width=200&height=200"
        return url
    }
}