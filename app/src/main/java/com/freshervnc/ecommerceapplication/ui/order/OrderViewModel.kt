package com.freshervnc.ecommerceapplication.ui.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.CreateOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateOrderResponse
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetOrderResponse
import com.freshervnc.ecommerceapplication.data.repository.ShoppingRepository
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class OrderViewModel(private val application: Application) : AndroidViewModel(application) {
    private var repository: ShoppingRepository = ShoppingRepository()

    private val createOrderResult = MutableLiveData<Event<Resource<CreateOrderResponse>>>()
    private val getCompressOrderResult = MutableLiveData<Event<Resource<GetOrderResponse>>>()
    private val getCompletedOrderResult = MutableLiveData<Event<Resource<GetOrderResponse>>>()
    private val getCancelOrderResult = MutableLiveData<Event<Resource<GetOrderResponse>>>()

    fun createOrderResult() : LiveData<Event<Resource<CreateOrderResponse>>>{
        return createOrderResult
    }

    fun getCompressOrderResult(): LiveData<Event<Resource<GetOrderResponse>>> {
        return getCompressOrderResult
    }
    fun getCompletedOrderResult() : LiveData<Event<Resource<GetOrderResponse>>>{
        return getCompletedOrderResult
    }
    fun getCancelOrderResult() : LiveData<Event<Resource<GetOrderResponse>>>{
        return getCancelOrderResult
    }

    fun getOrder(request: GetOrderRequest): Job = viewModelScope.launch {
        if (request.receiptStatus == 0) {
            getCompressOrderResult.postValue(Event(Resource.Loading()))
            try {
                if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                    val response = repository.getOrder(request)
                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            getCompressOrderResult.postValue(Event(Resource.Success(resultResponse)))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let {
                            val gson = Gson()
                            gson.fromJson(it.string(), ErrorResponse::class.java)
                        }
                        getCompressOrderResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                    }
                } else {
                    getCompressOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.no_internet_connection
                                ))))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        getCompressOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                        R.string.network_failure
                                    ))))
                    }

                    else -> {
                        getCompressOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                        R.string.conversion_error
                                    ))))
                    }
                }
            }
        }else if (request.receiptStatus == 1){
            getCompletedOrderResult.postValue(Event(Resource.Loading()))
            try {
                if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                    val response = repository.getOrder(request)
                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            getCompletedOrderResult.postValue(Event(Resource.Success(resultResponse)))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let {
                            val gson = Gson()
                            gson.fromJson(it.string(), ErrorResponse::class.java)
                        }
                        getCompletedOrderResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                    }
                } else {
                    getCompletedOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.no_internet_connection
                                ))))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        getCompletedOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                        R.string.network_failure
                                    ))))
                    }

                    else -> {
                        getCompletedOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                        R.string.conversion_error
                                    ))))
                    }
                }
            }
        }else{
            getCancelOrderResult.postValue(Event(Resource.Loading()))
            try {
                if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                    val response = repository.getOrder(request)
                    if (response.isSuccessful) {
                        response.body()?.let { resultResponse ->
                            getCancelOrderResult.postValue(Event(Resource.Success(resultResponse)))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let {
                            val gson = Gson()
                            gson.fromJson(it.string(), ErrorResponse::class.java)
                        }
                        getCancelOrderResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                    }
                } else {
                    getCancelOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection))))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        getCancelOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                        R.string.network_failure
                                    ))))
                    }

                    else -> {
                        getCancelOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error))))
                    }
                }
            }
        }
    }

    fun createOrder(request: CreateOrderRequest): Job = viewModelScope.launch {
        createOrderResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.createOrder(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        createOrderResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    createOrderResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                createOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    createOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.network_failure
                                ))))
                }
                else -> {
                    createOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error))))
                }
            }
        }
    }


}