package com.freshervnc.ecommerceapplication.data.repository

import com.freshervnc.ecommerceapplication.data.network.RetrofitInstance
import com.freshervnc.ecommerceapplication.model.DirectionsResponse

class MapRepository {
    suspend fun getDirections(origin: String , destination: String , apiKey: String) : DirectionsResponse = RetrofitInstance.mapService.getDirections(origin , destination , apiKey)
}