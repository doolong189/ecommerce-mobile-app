package com.freshervnc.ecommerceapplication.ui.mapbox

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.base.BaseLocationActivity
import com.freshervnc.ecommerceapplication.common.LocationPermissionHelper
import com.freshervnc.ecommerceapplication.databinding.ActivityLocationBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
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

class LocationActivity : BaseLocationActivity() , OnMapClickListener, OnMapLongClickListener {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var mapView: MapView
    private lateinit var dialogMap: AlertDialog.Builder
    private lateinit var ValuePointAnimation: ValueAnimator
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var floatingActionButtonUser: FloatingActionButton
    private lateinit var floatingActionButtonMap: FloatingActionButton
    private var listLocations = arrayListOf<Point>()
    private var listName = arrayListOf<String>()


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

                if (longitude != null && latitude != null && name != null) {
                    addAnnotationToMap(longitude, latitude, name)
                }

                binding.mapView.getMapboxMap().setCamera(
                    CameraOptions.Builder().center(Point.fromLngLat(longitude!!, latitude!!))
                        .build()
                )
            } else {
                onMapReady()
            }
        }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = findViewById(R.id.mapView)
        var contMap = 0

        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }

        binding.apply {
            mapView.getMapboxMap().apply {
                addOnMapLongClickListener(this@LocationActivity)
                addOnMapClickListener(this@LocationActivity)
            }
            toggle = ActionBarDrawerToggle(
                this@LocationActivity,
                binding.drawerLayout,
                R.string.open,
                R.string.close
            )
            binding.drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        floatingActionButtonUser = findViewById(R.id.navigationUser)
        floatingActionButtonUser.setOnClickListener {
            onMapReady()
        }
        floatingActionButtonMap = findViewById(R.id.changeMap)
        floatingActionButtonMap.setOnClickListener {
            contMap += 1
            if (contMap > 5) contMap = 1
            when (contMap) {
                1 -> mapView.getMapboxMap().loadStyleUri(Style.DARK)
                2 -> mapView.getMapboxMap().loadStyleUri(Style.OUTDOORS)
                3 -> mapView.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS)
                4 -> mapView.getMapboxMap().loadStyleUri(Style.SATELLITE)
                5 -> mapView.getMapboxMap().loadStyleUri(Style.TRAFFIC_NIGHT)
            }
        }

//        if (!MapboxNavigationApp.isSetup()) {
//            MapboxNavigationApp.setup {
//                NavigationOptions.Builder(context).build()
//            }
//        }
//        MapboxNavigationApp.attach(this)
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

    private fun addAnnotationToMapPulsing(long: Double, lat: Double, name: String) {
        bitmapFromDrawableRes(
            this,
            R.drawable.marker_purple
        )?.let {
            val annotationApi = binding.mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(long, lat))
                .withIconImage(it)
                .withTextField(name)
                .withTextColor(getColor(R.color.white))
                .withTextSize(20.5)
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
        pulsingLayerColor(74.0, 5.0, "ss")
    }


    private fun pulsingLayerColor(long: Double, lat: Double, name: String) {
        var pointLayer = ValueAnimator.ofObject(
            ArgbEvaluator(),
            Color.parseColor("#ec8a8a"), // Brighter shade
            Color.parseColor("#de3232") // Darker shade
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
        // Đặt ID tài nguyên cho các đối tượng thay vì Drawable
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
            this.pulsingMaxRadius = 80.0F
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
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
            //DataBase
//            val db = SaveLocationEntity(this@MainActivity)
//            db.insertLocation(textEdit, point.longitude(), point.latitude())
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