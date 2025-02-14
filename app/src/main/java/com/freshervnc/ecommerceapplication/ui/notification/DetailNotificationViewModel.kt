package com.freshervnc.ecommerceapplication.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailNotificationResponse
import com.freshervnc.ecommerceapplication.data.repository.NotificationRepository
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class DetailNotificationViewModel(private val application: Application) : AndroidViewModel(application) {
    private var repository : NotificationRepository = NotificationRepository()
    private val getDetailNotificationResult = MutableLiveData<Event<Resource<GetDetailNotificationResponse>>>()
    fun getDetailNotificationResult() : LiveData<Event<Resource<GetDetailNotificationResponse>>> {
        return getDetailNotificationResult
    }

    fun getDetailNotification(request : GetDetailNotificationRequest) : Job = viewModelScope.launch {
        getDetailNotificationResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getDetailNotification(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getDetailNotificationResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getDetailNotificationResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                getDetailNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getDetailNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getDetailNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }
}