package com.freshervnc.ecommerceapplication.ui.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.enity.GetNeedTokenRequest
import com.freshervnc.ecommerceapplication.data.enity.GetNeedTokenResponse
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.repository.UserRepository
import com.freshervnc.ecommerceapplication.data.model.UserInfo
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel(private val application: Application)  : AndroidViewModel(application) {
    var userInfo: UserInfo? = null
    private var repository : UserRepository = UserRepository()
    private val getAllUserResult = MutableLiveData<Event<Resource<GetAllUserResponse>>>()
    private val getUserInfoResult = MutableLiveData<Event<Resource<GetUserInfoResponse>>>()
    private val getNeedTokenResult = MutableLiveData<Event<Resource<GetNeedTokenResponse>>>()
    fun getAllUserResult(): LiveData<Event<Resource<GetAllUserResponse>>> { return getAllUserResult }

    fun getUserInfoResult(): LiveData<Event<Resource<GetUserInfoResponse>>> {
        return getUserInfoResult
    }

    fun getAllUser(request : GetAllUserRequest) : Job = viewModelScope.launch{
        getAllUserResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getAllUser(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        getAllUserResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getAllUserResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                getAllUserResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    getAllUserResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getAllUserResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun getNeedTokenResult() : LiveData<Event<Resource<GetNeedTokenResponse>>>{
        return getNeedTokenResult
    }

    fun getUserInfo(request : GetUserInfoRequest) : Job = viewModelScope.launch {
        getUserInfoResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getUserInfo(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        getUserInfoResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                        getUserInfoResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                getUserInfoResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    getUserInfoResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getUserInfoResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun getNeedToken(request : GetNeedTokenRequest) : Job = viewModelScope.launch {
        getNeedTokenResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getNeedToken(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        getNeedTokenResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getNeedTokenResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                getNeedTokenResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    getNeedTokenResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getNeedTokenResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun convertUserInfo(response : UserInfo) {
        userInfo = UserInfo(
            _id = response._id,
            name = response.name,
            address = response.address,
            password= response.password,
            email= response.email,
            phone = response.phone,
            image = response.image,
            loc = response.loc,
            token = response.token
        )
    }
    fun createGeUserInfoRequest(tmpEKycUserInfo: UserInfo): GetAllUserRequest {
        return GetAllUserRequest(
            id = tmpEKycUserInfo._id
        )
    }


}