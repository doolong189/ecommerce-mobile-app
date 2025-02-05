package com.freshervnc.ecommerceapplication.ui.findmap

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.CategoryAdapter
import com.freshervnc.ecommerceapplication.data.enity.GetCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryResponse
import com.freshervnc.ecommerceapplication.databinding.ActivityMapEcommerceBinding
import com.freshervnc.ecommerceapplication.databinding.ViewMapMarkerBinding
import com.freshervnc.ecommerceapplication.model.MapMarker
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingViewModel
import com.freshervnc.ecommerceapplication.utils.Contacts
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils.dpToPx
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.android.clustering.ClusterManager
import com.squareup.picasso.Picasso
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.random.Random


class MapEcommerceActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding : ActivityMapEcommerceBinding
    private lateinit var API_KEY : String
    private lateinit var gMap : GoogleMap
    private lateinit var preferences : PreferencesUtils
    private var locationArrayList: ArrayList<MapMarker>? = null
    private val shoppingViewModel by viewModels<ShoppingViewModel>()
    private var categoryAdapter = CategoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapEcommerceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setView()
        setObserve()
        setAction()
    }

    private fun initView(){
        API_KEY = resources.getString(R.string.API_KEY)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment_activity_users_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, this.API_KEY)
        }
        preferences = PreferencesUtils(this)
    }

    private fun setView(){
        binding.mapRcCategory.layoutManager = LinearLayoutManager(this, HORIZONTAL,false)
        binding.mapRcCategory.run { adapter = CategoryAdapter().also { categoryAdapter = it } }

        shoppingViewModel.getCategory()
    }


    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        val builder = LatLngBounds.Builder()
        builder.include(LatLng(preferences.getUserLoc()!![1], preferences.getUserLoc()!![0]))
        val bounds = builder.build()
        val padding = 200
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        gMap.moveCamera(cameraUpdate)
        //addMarkers()
    }


    @SuppressLint("PotentialBehaviorOverride")
    private fun addMarkers() {
        locationArrayList = ArrayList()

        val builder = LatLngBounds.Builder()
        locationArrayList!!.map {
            customMarker(it)
        }
        for (item in locationArrayList!!) {
            builder.include(LatLng(item.location.latitude, item.location.longitude))
        }
        gMap.setOnMarkerClickListener { marker ->
            showBottomSheetDialog(marker)
            true
        }
        val bounds = builder.build()
        val padding = 200
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        gMap.moveCamera(cameraUpdate)
    }

    private fun setAction(){
        categoryAdapter.onClickItemCategory { item, position ->
            shoppingViewModel.getProductWithCategory(GetProductWithCategoryRequest(idCategory = item._id , idUser = preferences.userId))
        }
    }
    private fun setObserve(){
        shoppingViewModel.getCategoryResult().observe(this, Observer {
            getCategoryResult(it)
        })

        shoppingViewModel.getProductWithCategoryResult().observe(this , Observer {
            getProductWithCategoryResult(it)
        })
    }

    private fun getCategoryResult(event: Event<Resource<GetCategoryResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.mapPgBar.visibility = View.GONE
                    response.data?.let {
                        categoryAdapter.submitList(it.categorys!!)
                    }
                }
                is Resource.Error -> {
                    binding.mapPgBar.visibility = View.GONE

                }
                is Resource.Loading -> {
                    binding.mapPgBar.visibility = View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun getProductWithCategoryResult(event : Event<Resource<GetProductWithCategoryResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when(response){
                is Resource.Success -> {
                    gMap.clear()
                    binding.mapPgBar.visibility = View.GONE
                    locationArrayList = ArrayList()
                    response.data?.products?.map {
                        locationArrayList!!.add(
                             MapMarker(
                                icon = MapMarker.Icon.Placeholder(url = it.image),
                                titleText = it.idUser?.name ?: "",
                                pinColor = COLORS.random(),
                                location = LatLng(it.idUser?.loc!![1], it.idUser.loc[0])
                            )
                        )
                    }
                    setMarker(locationArrayList!!)
                }
                is Resource.Loading -> {
                    binding.mapPgBar.visibility = View.GONE
                }
                is Resource.Error -> {
                    binding.mapPgBar.visibility = View.GONE
                    response.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setMarker(list : List<MapMarker>){
        val builder = LatLngBounds.Builder()
        if (list.isNotEmpty()) {
            list.map {
                customMarker(it)
            }
            for (item in list) {
                builder.include(LatLng(item.location.latitude, item.location.longitude))
            }
        }else{
            builder.include(LatLng(preferences.getUserLoc()!![1], preferences.getUserLoc()!![0]))
        }
        gMap.setOnMarkerClickListener { marker ->
            showBottomSheetDialog(marker)
            true
        }
        val bounds = builder.build()
        val padding = 200
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        gMap.moveCamera(cameraUpdate)
    }

    private fun customMarker(item : MapMarker){
        val binding by lazy { ViewMapMarkerBinding.inflate(LayoutInflater.from(applicationContext)) }
        val size = dpToPx(applicationContext, 40).toInt()
        Glide.with(this)
            .asBitmap()
            .load(item.icon.url)
            .override(size,size)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.mapMarkerViewIcon.setImageBitmap(resource)
                    binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    binding.root.layout(0, 0, binding.root.measuredWidth, binding.root.measuredHeight)
                    val bitmap = Bitmap.createBitmap(binding.root.measuredWidth, binding.root.measuredHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    binding.root.draw(canvas)
                    gMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(item.location.latitude, item.location.longitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        ImageViewCompat.setImageTintList(binding.mapMarkerViewPin, ColorStateList.valueOf(item.pinColor))
    }

    private fun showBottomSheetDialog(marker: Marker): Boolean {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)

        // Cập nhật thông tin tọa độ vào TextView
        val titleTextView = view.findViewById<TextView>(R.id.location_title)
        val coordinatesTextView = view.findViewById<TextView>(R.id.location_coordinates)
        val closeButton = view.findViewById<Button>(R.id.btn_close)

        titleTextView.text = "Location Selected"
        coordinatesTextView.text = "Latitude: ${marker.position.latitude}\nLongitude: ${marker.position.longitude}"

        // Sự kiện đóng dialog khi nhấn nút
        closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        return false
    }

    companion object{
        private val COLORS = listOf(
            Color.parseColor("#f44336"),
            Color.parseColor("#ffc107"),
            Color.parseColor("#ff5722"),
            Color.parseColor("#a11241"),
            Color.parseColor("#ff6c00"),
            Color.parseColor("#82800c"),
            Color.parseColor("#1b1ebc"),
            Color.parseColor("#613cb0"),
            Color.parseColor("#186332"),
            Color.parseColor("#329462"),
        )
    }


}