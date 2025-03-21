package com.freshervnc.ecommerceapplication.ui.findmap

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.android.volley.toolbox.StringRequest
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
import com.freshervnc.ecommerceapplication.dialog.DialogBottomDetailProduct
import com.freshervnc.ecommerceapplication.model.Product
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingViewModel
import com.freshervnc.ecommerceapplication.ui.messaging.MessageActivity
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils.dpToPx
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONObject

class MapEcommerceActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding : ActivityMapEcommerceBinding
    private lateinit var gMap : GoogleMap
    private lateinit var preferences : PreferencesUtils
    private val shoppingViewModel by viewModels<ShoppingViewModel>()
    private var categoryAdapter = CategoryAdapter()
    private val mapViewModel by viewModels<MapEcommerceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapEcommerceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()
        initView()
        setView()
        setObserve()
        setAction()
    }

    private fun initView(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment_activity_users_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.API_KEY))
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
        val latLngOrigin = LatLng(preferences.getUserLoc()!![1], preferences.getUserLoc()!![0])
        val bounds = builder.build()
        val padding = 5
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 14.5f))

//        this.gMap = p0
        // Sample coordinates
//        val latLngOrigin = LatLng(10.3181466, 123.9029382) // Ayala
//        val latLngDestination = LatLng(10.311795,123.915864) // SM City
//        this.gMap!!.addMarker(MarkerOptions().position(latLngOrigin).title("Ayala"))
//        this.gMap!!.addMarker(MarkerOptions().position(latLngDestination).title("SM City"))
//        this.gMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 14.5f))
//        val path: MutableList<List<LatLng>> = ArrayList()
//        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=10.3181466,123.9029382&destination=10.311795,123.915864&key="
//        val directionsRequest = object : StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> {
//                response ->
//            val jsonResponse = JSONObject(response)
//            // Get routes
//            val routes = jsonResponse.getJSONArray("routes")
//            val legs = routes.getJSONObject(0).getJSONArray("legs")
//            val steps = legs.getJSONObject(0).getJSONArray("steps")
//            for (i in 0 until steps.length()) {
//                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
//                path.add(PolyUtil.decode(points))
//            }
//            for (i in 0 until path.size) {
//                this.gMap!!.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
//            }
//        }, Response.ErrorListener {
//                _ ->
//        }){}
//        val requestQueue = Volley.newRequestQueue(this)
//        requestQueue.add(directionsRequest)
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

        mapViewModel.getDirectionsResult().observe(this, Observer { path ->
            path?.let {
                for (points in it) {
                    gMap.addPolyline(PolylineOptions().addAll(points).color(Color.RED))
                }
            }
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

    private fun getProductWithCategoryResult(event : Event<Resource<GetProductWithCategoryResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when(response){
                is Resource.Success -> {
                    gMap.clear()
                    binding.mapPgBar.visibility = View.GONE
                    response.data?.let {
                        if (it.products!!.isNotEmpty()) {
                            setMarker(it.products!!)
                        }else{
                            Toast.makeText(this,"Không tìm thấy sản phẩm",Toast.LENGTH_SHORT).show()
                        }
                    }
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
    private fun setMarker(list : List<Product>){
        val builder = LatLngBounds.Builder()
        list.map {
            customMarker(it)
            builder.include(LatLng(it.idUser?.loc?.get(1) ?: 0.0, it.idUser?.loc?.get(0) ?: 0.0))
            val bounds = builder.build()
            val padding = 200
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
//          gMap.moveCamera(cameraUpdate)
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.idUser?.loc?.get(1) ?: 0.0, it.idUser?.loc?.get(0) ?:  0.0), 14.5f))
        }
        gMap.setOnMarkerClickListener { marker ->
            val item = marker.tag as? Product
            if (item != null) {
                showBottomSheet(item)
            }
            true
        }
    }

    private fun customMarker(item : Product){
        val binding by lazy { ViewMapMarkerBinding.inflate(LayoutInflater.from(applicationContext)) }
        val size = dpToPx(applicationContext, 40).toInt()
        Glide.with(this)
            .asBitmap()
            .load(item.image)
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
                    val marker = gMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(item.idUser?.loc?.get(1) ?: 0.0, item.idUser?.loc?.get(0) ?: 0.0))
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    )
                    marker?.tag = item
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        ImageViewCompat.setImageTintList(binding.mapMarkerViewPin, ColorStateList.valueOf(Color.parseColor("#F44336")))
    }

    private fun showBottomSheet(item: Product) {
        val bottomSheet = DialogBottomDetailProduct.newInstance(item)
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        bottomSheet.setOnClickListener(object :
            DialogBottomDetailProduct.OnClickListener {
            override fun onClickListener(loc: List<Double>) {
                val latLngOrigin = LatLng(preferences.getUserLoc()?.get(1) ?: 0.0, preferences.getUserLoc()?.get(0) ?: 0.0)
                val latLngDestination = LatLng(loc[1],loc[0])
                gMap.addMarker(MarkerOptions().position(latLngOrigin).title(""))
                gMap.addMarker(MarkerOptions().position(latLngDestination).title(preferences.userName.toString()))
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 14.5f))
                val path: MutableList<List<LatLng>> = ArrayList()
                val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=${preferences.getUserLoc()?.get(1)},${preferences.getUserLoc()?.get(0)}&destination=${loc[1]},${loc[0]}&key=${getString(R.string.API_KEY_GOOGLE_MAP)}"
                val directionsRequest = object : StringRequest(Method.GET, urlDirections, Response.Listener<String> {
                        response ->
                    val jsonResponse = JSONObject(response)
                    val routes = jsonResponse.getJSONArray("routes")
                    val legs = routes.getJSONObject(0).getJSONArray("legs")
                    val steps = legs.getJSONObject(0).getJSONArray("steps")
                    for (i in 0 until steps.length()) {
                        val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                        path.add(PolyUtil.decode(points))
                    }
                    for (i in 0 until path.size) {
                        gMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
                    }
                }, Response.ErrorListener {
                        _ ->
                }){}
                val requestQueue = Volley.newRequestQueue(this@MapEcommerceActivity)
                requestQueue.add(directionsRequest)
            }

            override fun onClickSendMessageListener(id: String) {
                val mIntent = Intent(applicationContext,MessageActivity::class.java)
                val bundle = Bundle().apply {
                    putString("move_to", MoveTo.Map.toString())
                    putString("userId", item.idUser?._id)
                }
                mIntent.putExtras(bundle)
                startActivity(mIntent)
                bottomSheet.dismiss()
            }
        })
    }

    enum class MoveTo(val value: String) {
        Map("Map")
        ;
    }
}