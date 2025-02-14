package com.freshervnc.ecommerceapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.TypedValue
import androidx.annotation.Dimension
import androidx.annotation.RequiresApi
import com.freshervnc.ecommerceapplication.app.MyApplication
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object Utils {
    fun hasInternetConnection(application: MyApplication): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun formatPrice(price: Double): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(price)
    }

    fun dpToPx(context: Context, @Dimension(unit = Dimension.DP) dp: Int): Float {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertLongToTime(time: Long): String {
        val instant = Instant.ofEpochMilli(time) // Chuyển timestamp thành Instant
        // Định dạng ngày/tháng/năm theo múi giờ mặc định (hoặc có thể đặt ZoneId cụ thể)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = instant.atZone(ZoneId.systemDefault()).toLocalDate().format(formatter)
        return date
    }
}