package com.freshervnc.ecommerceapplication.ui.mapbox

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.CategoryAdapter
import com.freshervnc.ecommerceapplication.common.BaseLocationActivity
import com.freshervnc.ecommerceapplication.common.LocationPermissionHelper
import com.freshervnc.ecommerceapplication.data.enity.GetCategoryResponse
import com.freshervnc.ecommerceapplication.databinding.ActivityMapboxBinding
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingViewModel
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import java.lang.ref.WeakReference

class MapboxActivity : BaseLocationActivity() , OnMapClickListener, OnMapLongClickListener {

    private lateinit var binding: ActivityMapboxBinding
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var mapView: MapView
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private var listLocations = arrayListOf<Point>()
    private var listName = arrayListOf<String>()
    private val shoppingViewModel by viewModels<ShoppingViewModel>()
    private var categoryAdapter = CategoryAdapter()

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                val longitude = activityResult.data?.getDoubleExtra("longitude", 105.802682)
                val latitude = activityResult.data?.getDoubleExtra("latitude", 21.024955)
                val name = activityResult.data?.getStringExtra("name")
                if (longitude != null && latitude != null && name != null) { addAnnotationToMap(longitude, latitude, name) }
                binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(Point.fromLngLat(longitude!!, latitude!!)).build())
            } else {
                onMapReady()
            }
        }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) { onCameraTrackingDismissed() }
        override fun onMove(detector: MoveGestureDetector): Boolean { return false }
        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapboxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = findViewById(R.id.mapView)
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
        binding.apply {
            mapView.getMapboxMap().apply {
                addOnMapLongClickListener(this@MapboxActivity)
                addOnMapClickListener(this@MapboxActivity)
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        initView()
        setObserve()
    }

    private fun addAnnotationToMap(long: Double, lat: Double, name: String) {
        bitmapFromDrawableRes(
            this,
            R.drawable.red_marker
        )?.let {
            val annotationApi = binding.mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(long, lat))
                .withIconImage(it)
                .withTextField(name)
                .withTextColor(getColor(R.color.colorAccent))
                .withTextSize(12.0)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
        Log.d("Listafavo", "$listLocations , $listName")
    }

    private fun onMapReady() {
        addAnnotationToMap(105.807561,21.028026, "1128B Đ. La Thành")
        addAnnotationToMap(105.806976, 21.030118, "Lake Thu Le")
        addAnnotationToMap(105.803609, 21.023444, "159-157 P. Chùa Láng")
        addAnnotationToMap(105.804427, 21.027202, "DAC Data Technology")
        viewAnnotationManager = binding.mapView.viewAnnotationManager
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(12.0)
                .build()
        )
        initLocationComponent()
        setupGesturesListener()
        animateCameraDelayed()
        //test
        pulsingLayerColor()
    }

    private fun pulsingLayerColor() {
        val pointLayer = ValueAnimator.ofObject(
            ArgbEvaluator(),
            Color.parseColor("#ec8a8a"),
            Color.parseColor("#de3232")
        )
        pointLayer.setDuration(1000)
        pointLayer.repeatCount = ValueAnimator.INFINITE
        pointLayer.repeatMode = ValueAnimator.REVERSE
    }

    fun onCameraTrackingDismissed() {
        mapView.location.removeOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        mapView.location.removeOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
            this.pulsingMaxRadius = 50.0F
            this.pulsingColor = Color.parseColor("#FF3F51B5")
            locationPuck = LocationPuck2D(
                bearingImage = ImageHolder.from(R.drawable.mapbox_user_puck_icon),
                shadowImage = ImageHolder.from(R.drawable.mapbox_user_icon_shadow),
                scaleExpression = Expression.interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }

        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun animateCameraDelayed() {
        mapView.camera.apply {
            val bearing =
                createBearingAnimator(CameraAnimatorOptions.cameraAnimatorOptions(-45.0)) {
                    duration = 400
                    interpolator = AccelerateDecelerateInterpolator()
                }
            val zoom = createZoomAnimator(
                CameraAnimatorOptions.cameraAnimatorOptions(14.0) {
                    startValue(3.0)
                }
            ) {
                duration = 400
                interpolator = AccelerateDecelerateInterpolator()
            }
            val pitch = createPitchAnimator(
                CameraAnimatorOptions.cameraAnimatorOptions(40.0) {
                    startValue(0.0)
                }
            ) {
                duration = 400
                interpolator = AccelerateDecelerateInterpolator()
            }
            playAnimatorsSequentially(zoom, pitch, bearing)
        }
    }

    @SuppressLint("Request map")
    override fun onMapLongClick(point: Point): Boolean {
        var dialog = AlertDialog.Builder(this)
        var editText = EditText(this)
        editText.hint = getString(R.string.put_name_location)
        dialog.setView(editText)
        dialog.setTitle(getString(R.string.title_put_name))
        dialog.setPositiveButton(getString(R.string.accept)) { dialog, id ->
            var textEdit = editText.editableText.toString()
            listLocations.add(point)
            listName.add(textEdit.toString())
            addAnnotationToMap(point.longitude(), point.latitude(), textEdit)
        }
        dialog.setNegativeButton(getString(R.string.cancel)) { dialog, id ->
            dialog.cancel()
        }
        var alertDialog = dialog.create()
        alertDialog.show()
        alertDialog.setCancelable(false)
        return true
    }

    @SuppressLint("Request map")
    override fun onMapClick(point: Point): Boolean {
        return false
    }

    private fun initView(){
        binding.mapRcCategory.layoutManager = LinearLayoutManager(this, HORIZONTAL,false)
        binding.mapRcCategory.run { adapter = CategoryAdapter().also { categoryAdapter = it } }

        shoppingViewModel.getCategory()
    }

    private fun setObserve(){
        shoppingViewModel.getCategoryResult().observe(this, Observer {
            getCategoryResult(it)
        })
    }

    private fun getCategoryResult(event: Event<Resource<GetCategoryResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        categoryAdapter.submitList(it.categorys!!)
                    }
                }
                is Resource.Error -> {

                }
                is Resource.Loading -> {
                }
            }
        }
    }


    @SuppressLint("Lifecycle")
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    @SuppressLint("Lifecycle")
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("Lifecycle")
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }
}