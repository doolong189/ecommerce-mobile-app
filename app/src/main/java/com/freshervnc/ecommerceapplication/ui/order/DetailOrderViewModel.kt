package com.freshervnc.ecommerceapplication.ui.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.CreateOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailOrderResponse
import com.freshervnc.ecommerceapplication.data.repository.ShoppingRepository
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class DetailOrderViewModel(private val application: Application) : AndroidViewModel(application) {
    private val repository : ShoppingRepository = ShoppingRepository()

    private val getDetailOrderResult = MutableLiveData<Event<Resource<GetDetailOrderResponse>>>()

    fun getDetailOrderResult(): LiveData<Event<Resource<GetDetailOrderResponse>>> {
        return getDetailOrderResult
    }

    fun getDetailOrder(request: GetDetailOrderRequest): Job = viewModelScope.launch {
        getDetailOrderResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getDetailOrder(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        getDetailOrderResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getDetailOrderResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                getDetailOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    getDetailOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure
                    ))))
                }
                else -> {
                    getDetailOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error))))
                }
            }
        }
    }
}