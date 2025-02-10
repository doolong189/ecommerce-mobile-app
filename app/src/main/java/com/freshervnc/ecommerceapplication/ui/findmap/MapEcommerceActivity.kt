package com.freshervnc.ecommerceapplication.ui.findmap

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.CategoryAdapter
import com.freshervnc.ecommerceapplication.adapter.ProductAdapter
import com.freshervnc.ecommerceapplication.data.enity.GetCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductSimilarRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryResponse
import com.freshervnc.ecommerceapplication.databinding.ActivityMapEcommerceBinding
import com.freshervnc.ecommerceapplication.databinding.BottomDialogInfoProductBinding
import com.freshervnc.ecommerceapplication.databinding.ViewMapMarkerBinding
import com.freshervnc.ecommerceapplication.model.Product
import com.freshervnc.ecommerceapplication.model.UserInfo
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingViewModel
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetDialog


class MapEcommerceActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding : ActivityMapEcommerceBinding
    private lateinit var gMap : GoogleMap
    private lateinit var preferences : PreferencesUtils
    private val shoppingViewModel by viewModels<ShoppingViewModel>()
    private var categoryAdapter = CategoryAdapter()
    private var productAdapter = ProductAdapter()
    private var mMarker : Marker? = null
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

        shoppingViewModel.getDetailProductResult().observe(this, Observer {
            getDetailProductResult(it)
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
                    setMarker(response.data?.products!!)
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

    private fun getDetailProductResult(event: Event<Resource<GetDetailProductResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Error -> {
                    binding.mapPgBar.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.mapPgBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.mapPgBar.visibility = View.GONE
                    showBottomSheetDialog(response.data?.data!!)
                }
            }
        }
    }

    private fun setMarker(list : List<Product>){
        val builder = LatLngBounds.Builder()
        list.map {
            customMarker(it)
            builder.include(LatLng(it.idUser?.loc?.get(1) ?: 0.0, it.idUser?.loc?.get(0) ?: 0.0))
        }
        val bounds = builder.build()
        val padding = 200
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        gMap.moveCamera(cameraUpdate)
    }


    @SuppressLint("PotentialBehaviorOverride")
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
                    mMarker = gMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(item.idUser?.loc?.get(1) ?: 0.0, item.idUser?.loc?.get(0) ?: 0.0))
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                    mMarker!!.tag = item._id
                    Log.e("zzzzz","${mMarker!!.tag}")
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        ImageViewCompat.setImageTintList(binding.mapMarkerViewPin, ColorStateList.valueOf(COLORS[0]))

        gMap.setOnMarkerClickListener { marker ->
            Log.e("zzzzz","${mMarker!!.tag}")
            shoppingViewModel.getDetailProduct(GetDetailProductRequest(id = mMarker!!.tag.toString()))
            true
        }
    }

    private fun showBottomSheetDialog(item : Product): Boolean {
        val bottomSheetDialog = BottomSheetDialog(this)
        val binding = BottomDialogInfoProductBinding.inflate(layoutInflater, null, false)
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()

        //init view
        binding.rcSimilarProduct.layoutManager = GridLayoutManager(this, 2)
        binding.rcSimilarProduct.run { adapter = ProductAdapter().also { productAdapter = it } }
        shoppingViewModel.getProductSimilar(GetProductSimilarRequest(idUser = preferences.userId.toString() , idProduct = item._id))

        //set view
        binding.tvProduct.text = item.name
        binding.tvStore.text = "Cửa hàng: "+item.idUser?.name

        //set Observe
        shoppingViewModel.getProductSimilarResult().observe(this, Observer {
            it.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Error -> {
                        binding.pbBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.pbBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.pbBar.visibility = View.GONE
                        response.data?.let { resultResponse ->
                            productAdapter.submitList(resultResponse.products!!)
                        }
                    }
                }
            }
        })
        return false
    }

    companion object{
        private val COLORS = listOf(
            Color.parseColor("#F44336")
        )
    }


}