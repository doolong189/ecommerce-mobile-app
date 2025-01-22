package com.freshervnc.ecommerceapplication.ui.findmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.freshervnc.ecommerceapplication.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places

class MapEcommerceActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var API_KEY : String
    private lateinit var gMap : GoogleMap
    var sydney: LatLng = LatLng(21.028050, 105.801532)
    var TamWorth: LatLng = LatLng(21.023443, 105.802734)
    var NewCastle: LatLng = LatLng(21.028691, 105.810158)
    var Brisbane: LatLng = LatLng(21.034058, 105.802390)
    private var locationArrayList: ArrayList<LatLng>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map_ecommerce)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView(){
        API_KEY = resources.getString(R.string.API_KEY)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment_activity_users_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, this.API_KEY)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        addMarkers()
    }

    private fun addMarkers() {
        locationArrayList = ArrayList()
        locationArrayList!!.add(sydney)
        locationArrayList!!.add(TamWorth)
        locationArrayList!!.add(NewCastle)
        locationArrayList!!.add(Brisbane)
        val builder = LatLngBounds.Builder()
        for (item in locationArrayList!!) {
            val location = LatLng(item.latitude, item.longitude)
            gMap.addMarker(MarkerOptions().position(location).title("User"))
                ?.setIcon(bitmapFromVector(this@MapEcommerceActivity,
                    R.drawable.baseline_person_24
                ))
        }
        for (item in locationArrayList!!) {
            builder.include(LatLng(item.latitude, item.longitude))
        }
        val bounds = builder.build()
        val padding = 200
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        gMap.moveCamera(cameraUpdate)
    }

    fun bitmapFromVector(context : Context, vectorId : Int) : BitmapDescriptor? {
        val vectorDrawable : Drawable? = ContextCompat.getDrawable(context,vectorId)
        vectorDrawable?.setBounds(0,0,vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap : Bitmap? = vectorDrawable?.intrinsicHeight?.let {
            vectorDrawable.intrinsicWidth.let { it1 ->
                Bitmap.createBitmap(
                    it1,
                    it, Bitmap.Config.ARGB_8888)
            }
        }
        val convas : Canvas? = bitmap?.let { Canvas(it) }
        if (convas != null) {
            vectorDrawable.draw(convas)
        }
        return bitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }
    }

}