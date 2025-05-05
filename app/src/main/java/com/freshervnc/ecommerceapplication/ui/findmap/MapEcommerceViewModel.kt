package com.freshervnc.ecommerceapplication.ui.findmap

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.repository.MapRepository
import com.freshervnc.ecommerceapplication.data.model.DirectionsResponse
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class MapEcommerceViewModel(private val application: Application)  : AndroidViewModel(application) {
    private val repository = MapRepository()
    private val getDirectionsResult = MutableLiveData<List<List<LatLng>>>()
    fun getDirectionsResult(): LiveData<List<List<LatLng>>> {
        return getDirectionsResult
    }
    private val getDirectionsErrorResult = MutableLiveData<String>()
    fun getDirectionsErrorResult(): LiveData<String> {
        return getDirectionsErrorResult
    }
    fun fetchDirections(origin: String, destination: String, apiKey: String): Job = viewModelScope.launch {
            val response = repository.getDirections(origin, destination, apiKey)
        if (response != null) {
            val path = mutableListOf<List<LatLng>>()
            for (route in response.routes) {
                for (leg in route.legs) {
                    for (step in leg.steps) {
                        val points = step.polyline.points
                        path.add(PolyUtil.decode(points))
                    }
                }
            }
            getDirectionsResult.value = path
        } else {
            getDirectionsErrorResult.value = "Error fetching directions"
        }
    }
}