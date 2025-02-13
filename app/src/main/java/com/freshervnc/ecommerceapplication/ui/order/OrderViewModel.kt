package com.freshervnc.ecommerceapplication.ui.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.AddOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.AddOrderResponse
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

    private val addOrderResult = MutableLiveData<Event<Resource<AddOrderResponse>>>()
    private val getCompressOrderResult = MutableLiveData<Event<Resource<GetOrderResponse>>>()
    private val getCompletedOrderResult = MutableLiveData<Event<Resource<GetOrderResponse>>>()
    private val getCancelOrderResult = MutableLiveData<Event<Resource<GetOrderResponse>>>()

    fun addOrderResult() : LiveData<Event<Resource<AddOrderResponse>>>{
        return addOrderResult
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
                        getCompressOrderResult.postValue(
                            Event(
                                Resource.Error(
                                    errorResponse?.message ?: ""
                                )
                            )
                        )
                    }
                } else {
                    getCompressOrderResult.postValue(
                        Event(
                            Resource.Error(
                                getApplication<MyApplication>().getString(
                                    R.string.no_internet_connection
                                )
                            )
                        )
                    )
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        getCompressOrderResult.postValue(
                            Event(
                                Resource.Error(
                                    getApplication<MyApplication>().getString(
                                        R.string.network_failure
                                    )
                                )
                            )
                        )
                    }

                    else -> {
                        getCompressOrderResult.postValue(
                            Event(
                                Resource.Error(
                                    getApplication<MyApplication>().getString(
                                        R.string.conversion_error
                                    )
                                )
                            )
                        )
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
                        getCompletedOrderResult.postValue(
                            Event(
                                Resource.Error(
                                    errorResponse?.message ?: ""
                                )
                            )
                        )
                    }
                } else {
                    getCompletedOrderResult.postValue(
                        Event(
                            Resource.Error(
                                getApplication<MyApplication>().getString(
                                    R.string.no_internet_connection
                                )
                            )
                        )
                    )
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        getCompletedOrderResult.postValue(
                            Event(
                                Resource.Error(
                                    getApplication<MyApplication>().getString(
                                        R.string.network_failure
                                    )
                                )
                            )
                        )
                    }

                    else -> {
                        getCompletedOrderResult.postValue(
                            Event(
                                Resource.Error(
                                    getApplication<MyApplication>().getString(
                                        R.string.conversion_error
                                    )
                                )
                            )
                        )
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

    fun addOrder(request: AddOrderRequest): Job = viewModelScope.launch {
        addOrderResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.addOrder(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        addOrderResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    addOrderResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                addOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    addOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.network_failure
                                ))))
                }
                else -> {
                    addOrderResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error))))
                }
            }
        }
    }


}