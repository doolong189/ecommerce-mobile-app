package com.freshervnc.ecommerceapplication.ui.findmap

import android.content.Context
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
import kotlin.math.ceil


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
                ?.setIcon(
                    bitmapFromVector(this@MapEcommerceActivity,
                    R.drawable.baseline_person_24)
                )
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


    private fun createUserBitmap(vectorId: Int): Bitmap? {
        var result: Bitmap? = null
        try {
            result = Bitmap.createBitmap(dp(62f), dp(76f), Bitmap.Config.ARGB_8888)
            result.eraseColor(Color.TRANSPARENT)
            val canvas = Canvas(result)
            val drawable = resources.getDrawable(R.drawable.baseline_person_pin_24)
            drawable.setBounds(0, 0, dp(62f), dp(76f))
            drawable.draw(canvas)

            val roundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val bitmapRect = RectF()
            canvas.save()

            val bitmap = BitmapFactory.decodeResource(resources, vectorId)
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                val matrix: Matrix = Matrix()
                val scale: Float = dp(52f) / bitmap.width.toFloat()
                matrix.postTranslate(5f,5f)
                matrix.postScale(scale, scale)
                roundPaint.setShader(shader)
                shader.setLocalMatrix(matrix)
                bitmapRect[5f, 5f, 52f + 5f] = 52f + 5f
                canvas.drawRoundRect(bitmapRect, 26f, 26f, roundPaint)
            }
            canvas.restore()
            try {
                canvas.setBitmap(null)
            } catch (e: Exception) {
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result
    }

    fun dp(value: Float): Int {
        if (value == 0f) {
            return 0
        }
        return ceil((resources.displayMetrics.density * value).toDouble()).toInt()
    }
}