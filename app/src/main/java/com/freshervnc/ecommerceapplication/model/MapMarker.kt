package com.freshervnc.ecommerceapplication.model

import android.graphics.Bitmap
import androidx.annotation.ColorInt
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class MapMarker(
    val id : String,
    val icon: Icon,
    val titleText: String,
    @ColorInt val pinColor: Int,
    val location: LatLng
) : ClusterItem {

    override fun getPosition(): LatLng = location

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null

    override fun getZIndex(): Float?  = null

    sealed interface Icon {

        val url: String

        data class Placeholder(override val url: String) : Icon

        data class BitmapIcon(override val url: String, val image: Bitmap) : Icon
    }
}