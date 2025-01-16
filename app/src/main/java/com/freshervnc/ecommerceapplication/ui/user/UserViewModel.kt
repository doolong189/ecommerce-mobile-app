package com.freshervnc.ecommerceapplication.ui.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.repository.UserRepository
import com.freshervnc.ecommerceapplication.model.UserInfo
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

    fun getAllUserResult(): LiveData<Event<Resource<GetAllUserResponse>>> {
        return getAllUserResult
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

    class UserViewModelFactory(val application : Application) : ViewModelProvider.Factory{

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(UserViewModel::class.java)){
                UserViewModel(application) as T
            }else{
                throw IllegalArgumentException("viewmodel not found")
            }
        }
    }
}