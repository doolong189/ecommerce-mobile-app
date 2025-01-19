package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.GetProductRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance

class ShoppingRepository {
    suspend fun getCategory() = RetrofitInstance.api.getCategory()

    suspend fun getProduct(request : GetProductRequest) = RetrofitInstance.api.getProduct(request)
}