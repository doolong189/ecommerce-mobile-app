package com.freshervnc.ecommerceapplication.ui.launch.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.LoginResponse
import com.freshervnc.ecommerceapplication.data.enity.RegisterRequest
import com.freshervnc.ecommerceapplication.data.enity.RegisterResponse
import com.freshervnc.ecommerceapplication.data.repository.UserRepository
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class RegisterAccountViewModel (private val application: Application)  : AndroidViewModel(application) {
    private var repository : UserRepository = UserRepository()
    private val registerAccountResult = MutableLiveData<Event<Resource<RegisterResponse>>>()
    fun registerAccountResult(): LiveData<Event<Resource<RegisterResponse>>> {
        return registerAccountResult
    }

    fun registerAccount(request : RegisterRequest) : Job = viewModelScope.launch {
        registerAccountResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.registerAccount(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        registerAccountResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    registerAccountResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                registerAccountResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    registerAccountResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    registerAccountResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }
}