package com.freshervnc.ecommerceapplication.data.network

import com.freshervnc.ecommerceapplication.data.enity.AddCartRequest
import com.freshervnc.ecommerceapplication.data.enity.AddCartResponse
import com.freshervnc.ecommerceapplication.data.enity.AddNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.AddNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.CreateOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateOrderResponse
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.enity.GetCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartResponse
import com.freshervnc.ecommerceapplication.data.enity.GetCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailOrderResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.GetOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetOrderResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductSimilarRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductSimilarResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.enity.LoginResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.RegisterRequest
import com.freshervnc.ecommerceapplication.data.enity.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/user/getUsers")
    suspend fun getAllUser(@Body request : GetAllUserRequest) : Response<GetAllUserResponse>

    @POST("/user/login")
    suspend fun getLogin(@Body request : LoginRequest) : Response<LoginResponse>

    suspend fun getRegisterAccount(@Body request : RegisterRequest) : Response<RegisterResponse>

    @GET("category/getCategory")
    suspend fun getCategory() : Response<GetCategoryResponse>

    @POST("product/getProduct")
    suspend fun getProduct(@Body request : GetProductRequest) : Response<GetProductResponse>

    @POST("product/getDetailProduct")
    suspend fun getDetailProduct(@Body request : GetDetailProductRequest) : Response<GetDetailProductResponse>

    @POST("product/getProductWithCategory")
    suspend fun getProductWithCategory(@Body request : GetProductWithCategoryRequest) : Response<GetProductWithCategoryResponse>

    @POST("product/getProductSimilar")
    suspend fun getProductSimilar(@Body request : GetProductSimilarRequest) : Response<GetProductSimilarResponse>

    @POST("/user/getUserInfo")
    suspend fun getUserInfo(@Body request : GetUserInfoRequest) : Response<GetUserInfoResponse>

    @POST("ntf/pushNotification")
    suspend fun pushNotification(@Body request : PushNotificationRequest) : Response<PushNotificationResponse>

    @POST("ntf/addNotification")
    suspend fun addNotification(@Body request : AddNotificationRequest) : Response<AddNotificationResponse>

    @POST("ntf/getNotification")
    suspend fun getNotification(@Body request : GetNotificationRequest) : Response<GetNotificationResponse>

    @POST("ntf/getDetailNotification")
    suspend fun getDetailNotification(@Body request : GetDetailNotificationRequest) : Response<GetDetailNotificationResponse>

    @POST("cart/addCart")
    suspend fun addCart(@Body request : AddCartRequest) : Response<AddCartResponse>

    @POST("cart/getCart")
    suspend fun getCart(@Body request : GetCartRequest) : Response<GetCartResponse>

    @POST("order/createOrder")
    suspend fun createOrder(@Body request : CreateOrderRequest) : Response<CreateOrderResponse>

    @POST("order/getOrders")
    suspend fun getOrders(@Body request : GetOrderRequest) : Response<GetOrderResponse>

    @POST("order/getDetailOrders")
    suspend fun getDetailOrders(@Body request : GetDetailOrderRequest) : Response<GetDetailOrderResponse>
}