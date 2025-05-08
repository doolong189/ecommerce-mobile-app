package com.freshervnc.ecommerceapplication.ui.launch.verification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GenerateOtpRequest
import com.freshervnc.ecommerceapplication.data.enity.VerifyOtpRequest
import com.freshervnc.ecommerceapplication.data.enity.VerifyOtpResponse
import com.freshervnc.ecommerceapplication.data.repository.UserRepository
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class VerificationOtpViewModel(private val application: Application)  : AndroidViewModel(application) {
    private var repository : UserRepository = UserRepository()
    var email = ""
    private val verifyOtpResult = MutableLiveData<Event<Resource<VerifyOtpResponse>>>()
    fun verifyOtpResult(): LiveData<Event<Resource<VerifyOtpResponse>>> {
        return verifyOtpResult
    }
    fun verifyOtp(request : VerifyOtpRequest) : Job = viewModelScope.launch{
        verifyOtpResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.verifyOTP(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        verifyOtpResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    verifyOtpResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                verifyOtpResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    verifyOtpResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    verifyOtpResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }
}