package com.freshervnc.ecommerceapplication.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.AddCartRequest
import com.freshervnc.ecommerceapplication.data.enity.AddCartResponse
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartResponse
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

class CartViewModel(private val application: Application) : AndroidViewModel(application) {
    private var repository: ShoppingRepository = ShoppingRepository()

    private val addCartResult = MutableLiveData<Event<Resource<AddCartResponse>>>()

    fun addCartResult(): LiveData<Event<Resource<AddCartResponse>>> {
        return addCartResult
    }

    private val getCartResult = MutableLiveData<Event<Resource<GetCartResponse>>>()

    fun getCartResult(): LiveData<Event<Resource<GetCartResponse>>> {
        return getCartResult
    }

    fun addCart(request: AddCartRequest): Job = viewModelScope.launch {
        addCartResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.addCart(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        addCartResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    addCartResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                addCartResult.postValue(
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
                    addCartResult.postValue(
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
                    addCartResult.postValue(
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
    }

    fun getCart(request: GetCartRequest): Job = viewModelScope.launch {
        getCartResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getCart(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        getCartResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getCartResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                getCartResult.postValue(
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
                    getCartResult.postValue(
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
                    getCartResult.postValue(
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
    }

}
