package com.freshervnc.ecommerceapplication.ui.main.shopping

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetReviewWithProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetReviewWithProductResponse
import com.freshervnc.ecommerceapplication.data.repository.ShoppingRepository
import com.freshervnc.ecommerceapplication.utils.Contacts
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class DetailProductViewModel(private val application: Application)  : AndroidViewModel(application)  {
    private var repository : ShoppingRepository = ShoppingRepository()
    private val getGetDetailProductResult = MutableLiveData<Event<Resource<GetDetailProductResponse>>>()
    private val getReviewWithProductResult = MutableLiveData<Event<Resource<GetReviewWithProductResponse>>>()
    fun getGetDetailProductResult(): LiveData<Event<Resource<GetDetailProductResponse>>> {
        return getGetDetailProductResult
    }
    fun getReviewWithProductResult() : LiveData<Event<Resource<GetReviewWithProductResponse>>>{
        return getReviewWithProductResult
    }
    fun getGetDetailProduct(request : GetDetailProductRequest) : Job = viewModelScope.launch{
        getGetDetailProductResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getDetailProduct(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getGetDetailProductResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getGetDetailProductResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                getGetDetailProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getGetDetailProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getGetDetailProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }


    fun getReviewWithProduct(request : GetReviewWithProductRequest) : Job = viewModelScope.launch {
        getReviewWithProductResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getReviewWithProduct(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getReviewWithProductResult.postValue(Event(Resource.Success(resultResponse)))
                        Log.e(Contacts.TAG,resultResponse.message.toString())
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getReviewWithProductResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                    Log.e(Contacts.TAG,errorResponse?.message.toString())
                }
            }else{
                getReviewWithProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getReviewWithProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getReviewWithProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                    Log.e(Contacts.TAG, t.localizedMessage?.toString() ?: "")
                }
            }
        }
    }
}