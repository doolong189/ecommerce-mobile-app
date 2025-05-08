package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.GenerateOtpRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.enity.GetNeedTokenRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.enity.RegisterRequest
import com.freshervnc.ecommerceapplication.data.enity.VerifyOtpRequest
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance
import com.freshervnc.ecommerceapplication.data.model.UserInfo

class UserRepository {

    suspend fun getAllUser(request : GetAllUserRequest) = RetrofitInstance.apiService.getAllUser(request)

    suspend fun getLogin(request : LoginRequest) = RetrofitInstance.apiService.getLogin(request)

    suspend fun registerAccount(request : RegisterRequest) = RetrofitInstance.apiService.getRegisterAccount(request)

    suspend fun getUserInfo(request : GetUserInfoRequest) = RetrofitInstance.apiService.getUserInfo(request)

    suspend fun getNeedToken(request : GetNeedTokenRequest) = RetrofitInstance.apiService.getNeedToken(request)

    suspend fun generateOTP(request : GenerateOtpRequest) = RetrofitInstance.apiService.generateOTP(request)

    suspend fun verifyOTP(request : VerifyOtpRequest) = RetrofitInstance.apiService.verifyOTP(request)
    fun convertGetAllUser(response: GetAllUserResponse): List<UserInfo>? {
        var list: List<UserInfo>  = response.users ?: listOf<UserInfo>()
        return list.map {
            UserInfo(
                _id = it._id,
                name = it.name,
                address = it.address,
                password= it.password,
                email= it.email,
                phone = it.phone,
                image = it.image,
                loc = it.loc,
                token = it.token
            )
        }
    }

    fun convertUserInfo(response: GetUserInfoResponse): UserInfo {
        return UserInfo(
            _id = response.user?._id ?: "",
            name = response.user?.name ?: "",
            address = response.user?.address ?: "",
            password = response.user?.password ?: "",
            email = response.user?.email ?: "",
            phone = response.user?.phone ?: "",
            image = response.user?.image ?: "",
            loc = response.user?.loc ,
            token = response.user?.token ?: ""
        )
    }
}