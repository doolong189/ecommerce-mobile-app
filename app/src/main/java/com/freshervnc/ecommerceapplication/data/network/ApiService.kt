package com.freshervnc.ecommerceapplication.data.network

import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/user/getUsers/{id}")
    suspend fun requestGetAllUser(@Body request : GetAllUserRequest) : Response<GetAllUserResponse>
}