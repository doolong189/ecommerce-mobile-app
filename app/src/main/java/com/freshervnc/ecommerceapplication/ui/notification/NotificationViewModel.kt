package com.freshervnc.ecommerceapplication.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.CreateNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationResponse
import com.freshervnc.ecommerceapplication.data.repository.NotificationRepository
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class NotificationViewModel(private val application: Application)  : AndroidViewModel(application) {
    private var repository : NotificationRepository = NotificationRepository()
    private val pushNotificationResult = MutableLiveData<Event<Resource<PushNotificationResponse>>>()
    private val createNotificationResult = MutableLiveData<Event<Resource<CreateNotificationResponse>>>()
    private val getNotificationResult = MutableLiveData<Event<Resource<GetNotificationResponse>>>()

    val imageUrl = ""
    fun pushNotificationResult(): LiveData<Event<Resource<PushNotificationResponse>>> {
        return pushNotificationResult
    }

    fun createNotificationResult(): LiveData<Event<Resource<CreateNotificationResponse>>>{
        return createNotificationResult
    }

    fun getNotificationResult() : LiveData<Event<Resource<GetNotificationResponse>>>{
        return getNotificationResult
    }

    fun pushNotification(request : PushNotificationRequest) : Job = viewModelScope.launch{
        pushNotificationResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.pushNotification(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        pushNotificationResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    pushNotificationResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                pushNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    pushNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    pushNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun createNotification(request : CreateNotificationRequest) : Job = viewModelScope.launch{
        createNotificationResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.createNotification(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        createNotificationResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    createNotificationResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                createNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    createNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    createNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun getNotification(request : GetNotificationRequest) : Job = viewModelScope.launch {
        getNotificationResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getNotification(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getNotificationResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                        getNotificationResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                getNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }
}