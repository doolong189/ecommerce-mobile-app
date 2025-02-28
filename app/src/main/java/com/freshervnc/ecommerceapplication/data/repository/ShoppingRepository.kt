package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.AddCartRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.DeleteCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductSimilarRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryRequest
import com.freshervnc.ecommerceapplication.data.enity.UpdateCartRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance

class ShoppingRepository {
    suspend fun getCategory() = RetrofitInstance.api.getCategory()

    suspend fun getProduct(request : GetProductRequest) = RetrofitInstance.api.getProduct(request)

    suspend fun getDetailProduct(request : GetDetailProductRequest) = RetrofitInstance.api.getDetailProduct(request)

    suspend fun getProductSimilar(request : GetProductSimilarRequest) = RetrofitInstance.api.getProductSimilar(request)

    suspend fun getProductWithCategory(request: GetProductWithCategoryRequest) = RetrofitInstance.api.getProductWithCategory(request)

    suspend fun addCart(request : AddCartRequest) = RetrofitInstance.api.addCart(request)

    suspend fun getCart(request : GetCartRequest) = RetrofitInstance.api.getCart(request)

    suspend fun updateCart(request : UpdateCartRequest) = RetrofitInstance.api.updateCart(request)

    suspend fun deleteCart(request : DeleteCartRequest) = RetrofitInstance.api.deleteCart(request)

    suspend fun createOrder(request : CreateOrderRequest) = RetrofitInstance.api.createOrder(request)

    suspend fun getOrder(request : GetOrderRequest) = RetrofitInstance.api.getOrders(request)

    suspend fun getDetailOrder(request : GetDetailOrderRequest) = RetrofitInstance.api.getDetailOrders(request)
}