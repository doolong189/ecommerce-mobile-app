package com.freshervnc.ecommerceapplication.data.network

import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.enity.GetCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.enity.LoginResponse
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

    @POST("/user/getUserInfo")
    suspend fun getUserInfo(@Body request : GetUserInfoRequest) : Response<GetUserInfoResponse>

}