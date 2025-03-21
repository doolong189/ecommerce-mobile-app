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
    suspend fun getCategory() = RetrofitInstance.apiService.getCategory()

    suspend fun getProduct(request : GetProductRequest) = RetrofitInstance.apiService.getProduct(request)

    suspend fun getDetailProduct(request : GetDetailProductRequest) = RetrofitInstance.apiService.getDetailProduct(request)

    suspend fun getProductSimilar(request : GetProductSimilarRequest) = RetrofitInstance.apiService.getProductSimilar(request)

    suspend fun getProductWithCategory(request: GetProductWithCategoryRequest) = RetrofitInstance.apiService.getProductWithCategory(request)

    suspend fun addCart(request : AddCartRequest) = RetrofitInstance.apiService.addCart(request)

    suspend fun getCart(request : GetCartRequest) = RetrofitInstance.apiService.getCart(request)

    suspend fun updateCart(request : UpdateCartRequest) = RetrofitInstance.apiService.updateCart(request)

    suspend fun deleteCart(request : DeleteCartRequest) = RetrofitInstance.apiService.deleteCart(request)

    suspend fun createOrder(request : CreateOrderRequest) = RetrofitInstance.apiService.createOrder(request)

    suspend fun getOrder(request : GetOrderRequest) = RetrofitInstance.apiService.getOrders(request)

    suspend fun getDetailOrder(request : GetDetailOrderRequest) = RetrofitInstance.apiService.getDetailOrders(request)
}