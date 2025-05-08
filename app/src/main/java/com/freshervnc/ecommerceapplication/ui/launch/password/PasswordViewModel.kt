package com.freshervnc.ecommerceapplication.ui.launch.password

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GenerateOtpRequest
import com.freshervnc.ecommerceapplication.data.enity.GenerateOtpResponse
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.repository.UserRepository
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class PasswordViewModel(private val application: Application)  : AndroidViewModel(application) {
    private var repository : UserRepository = UserRepository()
    private val generateOtpResult = MutableLiveData<Event<Resource<GenerateOtpResponse>>>()
    fun generateOtpResult(): LiveData<Event<Resource<GenerateOtpResponse>>> {
        return generateOtpResult
    }

    fun generateOtp(request : GenerateOtpRequest) : Job = viewModelScope.launch{
        generateOtpResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.generateOTP(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        generateOtpResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    generateOtpResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                generateOtpResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    Log.e("zzzz",t.localizedMessage.toString())
                    generateOtpResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    Log.e("zzzz1",t.localizedMessage.toString())
                    generateOtpResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }
}