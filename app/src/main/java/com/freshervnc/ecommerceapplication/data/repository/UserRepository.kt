package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance
import com.freshervnc.ecommerceapplication.model.UserInfo

class UserRepository {

    suspend fun getAllUser(request : GetAllUserRequest) = RetrofitInstance.api.requestGetAllUser(request)

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

}