package com.freshervnc.ecommerceapplication.ui.main.shopping

import android.app.Application
import android.provider.ContactsContract.Contacts
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.util.Util
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductSimilarRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductSimilarResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.repository.ShoppingRepository
import com.freshervnc.ecommerceapplication.ui.launch.login.LoginViewModel
import com.freshervnc.ecommerceapplication.utils.Constants
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class ShoppingViewModel(private val application: Application)  : AndroidViewModel(application)  {
    private var repository : ShoppingRepository = ShoppingRepository()
    private val getCategoryResult = MutableLiveData <Event<Resource<GetCategoryResponse>>>()
    private val getProductResult = MutableLiveData <Event<Resource<GetProductResponse>>>()
    private val getProductWithCategoryResult = MutableLiveData<Event<Resource<GetProductWithCategoryResponse>>>()
    private val getDetailProductResult = MutableLiveData<Event<Resource<GetDetailProductResponse>>>()
    private val getProductSimilarResult = MutableLiveData<Event<Resource<GetProductSimilarResponse>>>()
    fun getCategoryResult(): LiveData<Event<Resource<GetCategoryResponse>>> {
        return getCategoryResult
    }
    fun getProductResult(): LiveData<Event<Resource<GetProductResponse>>> {
        return getProductResult
    }
    
    fun getProductWithCategoryResult() : LiveData<Event<Resource<GetProductWithCategoryResponse>>>{
        return getProductWithCategoryResult
    }

    fun getDetailProductResult() : LiveData<Event<Resource<GetDetailProductResponse>>>{
        return getDetailProductResult
    }

    fun getProductSimilarResult() : LiveData<Event<Resource<GetProductSimilarResponse>>>{
        return getProductSimilarResult
    }
    fun getCategory() : Job = viewModelScope.launch{
        getCategoryResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getCategory()
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getCategoryResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getCategoryResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                getCategoryResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getCategoryResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getCategoryResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun getProduct(request : GetProductRequest) : Job = viewModelScope.launch{
        getProductResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getProduct(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getProductResult.postValue(Event(Resource.Success(resultResponse)))
                        Log.e(Constants.TAG,"${resultResponse.message}")
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getProductResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                    Log.e(Constants.TAG,"${errorResponse?.message}")
                }
            }else{
                getProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }
    
    fun getProductWithCategory(request : GetProductWithCategoryRequest) : Job = viewModelScope.launch { 
        getProductWithCategoryResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())){
                val response = repository.getProductWithCategory(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getProductWithCategoryResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else{
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getProductWithCategoryResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                getProductWithCategoryResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getProductWithCategoryResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getProductWithCategoryResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun getDetailProduct(request : GetDetailProductRequest) : Job = viewModelScope.launch {
        getDetailProductResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())){
                val response = repository.getDetailProduct(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getDetailProductResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else{
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getDetailProductResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                getDetailProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getDetailProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getDetailProductResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun getProductSimilar(request : GetProductSimilarRequest) : Job = viewModelScope.launch {
        getProductSimilarResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())){
                val response = repository.getProductSimilar(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getProductSimilarResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else{
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getProductSimilarResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                getProductSimilarResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getProductSimilarResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getProductSimilarResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }
}