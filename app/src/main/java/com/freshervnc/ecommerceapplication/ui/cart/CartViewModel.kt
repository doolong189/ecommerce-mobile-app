package com.freshervnc.ecommerceapplication.ui.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.CreateCartRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateCartResponse
import com.freshervnc.ecommerceapplication.data.enity.DeleteCartRequest
import com.freshervnc.ecommerceapplication.data.enity.DeleteCartResponse
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartResponse
import com.freshervnc.ecommerceapplication.data.enity.UpdateCartRequest
import com.freshervnc.ecommerceapplication.data.enity.UpdateCartResponse
import com.freshervnc.ecommerceapplication.data.repository.ShoppingRepository
import com.freshervnc.ecommerceapplication.utils.Constants
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class CartViewModel(private val application: Application) : AndroidViewModel(application) {
    private var repository: ShoppingRepository = ShoppingRepository()

    private val createCartResult = MutableLiveData<Event<Resource<CreateCartResponse>>>()

    fun createCartResult(): LiveData<Event<Resource<CreateCartResponse>>> {
        return createCartResult
    }

    private val getCartResult = MutableLiveData<Event<Resource<GetCartResponse>>>()

    fun getCartResult(): LiveData<Event<Resource<GetCartResponse>>> {
        return getCartResult
    }

    private val updateCartResult = MutableLiveData<Event<Resource<UpdateCartResponse>>>()
    fun updateCartResult(): LiveData<Event<Resource<UpdateCartResponse>>> {
        return updateCartResult
    }

    private val deleteCartResult = MutableLiveData<Event<Resource<DeleteCartResponse>>>()
    fun deleteCartResult() : LiveData<Event<Resource<DeleteCartResponse>>>{
        return deleteCartResult
    }

    fun createCart(request: CreateCartRequest): Job = viewModelScope.launch {
        createCartResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.addCart(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        createCartResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    createCartResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                createCartResult.postValue(
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
                    createCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.network_failure
                                ))))
                }

                else -> {
                    createCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.conversion_error
                                ))))
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
                    Log.e(Constants.TAG,"${errorResponse?.message}")
                }
                Log.e(Constants.TAG,"${response.body()?.message}")
            } else {
                getCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                R.string.no_internet_connection
                            ))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    getCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.network_failure
                                ))))
                }

                else -> {
                    getCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.conversion_error
                                ))))
                }
            }
        }
    }

    fun updateCart(request : UpdateCartRequest) : Job = viewModelScope.launch {
        updateCartResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.updateCart(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        updateCartResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    updateCartResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                updateCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                R.string.no_internet_connection
                            ))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    updateCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.network_failure
                                ))))
                }

                else -> {
                    updateCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.conversion_error
                                ))))
                }
            }
        }
    }

    fun deleteCart(request : DeleteCartRequest) : Job = viewModelScope.launch {
        deleteCartResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.deleteCart(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        deleteCartResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    deleteCartResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                deleteCartResult.postValue(
                    Event(Resource.Error(getApplication<MyApplication>().getString(
                                R.string.no_internet_connection
                            ))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    deleteCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.network_failure
                                ))))
                }

                else -> {
                    deleteCartResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.conversion_error
                                ))))
                }
            }
        }
    }

}
