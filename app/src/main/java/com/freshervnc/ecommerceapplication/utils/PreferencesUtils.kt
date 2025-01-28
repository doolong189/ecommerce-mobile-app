package com.freshervnc.ecommerceapplication.utils

import android.content.Context
import android.content.SharedPreferences
import com.freshervnc.ecommerceapplication.model.UserInfo
import com.google.gson.Gson

class PreferencesUtils(context: Context) {
    // Initialize SharedPreferences
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Save user data
    fun saveUserData(
        user: UserInfo?,
        token : String?
    ) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, user?._id)
        editor.putString(KEY_USER_NAME, user?.name)
        editor.putString(KEY_USER_EMAIL, user?.email)
        editor.putString(KEY_USER_PASSWORD, user?.password)
        editor.putString(KEY_USER_PHONE, user?.phone)
        editor.putString(KEY_USER_ADDRESS, user?.address)
        editor.putString(KEY_USER_IMAGE, user?.image)
        val locJson = Gson().toJson(user?.loc)
        editor.putString(KEY_USER_LOC, locJson)
        editor.putString(KEY_TOKEN,token)
        editor.apply()
    }

    //
    fun savePassword(password : String){
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_PASSWORD, password)
        editor.apply()
    }

    val userId: String? get() = sharedPreferences.getString(KEY_USER_ID, null)

    val userName: String? get() = sharedPreferences.getString(KEY_USER_NAME, null)

    val password: String?
        get() = sharedPreferences.getString(KEY_USER_PASSWORD, null)
    val email: String?
        get() = sharedPreferences.getString(KEY_USER_EMAIL, null)

    val phone: String?
        get() = sharedPreferences.getString(KEY_USER_PHONE, null)
    val address: String?
        get() = sharedPreferences.getString(KEY_USER_ADDRESS, null)

    val image : String? get() = sharedPreferences.getString(KEY_USER_IMAGE,null)
    val token : String? get() = sharedPreferences.getString(KEY_TOKEN,null)
    fun getUserLoc(): List<Double>? {
        val locJson = sharedPreferences.getString(KEY_USER_LOC, null)
        return locJson?.let {
            Gson().fromJson(it, Array<Double>::class.java).toList()
        }
    }

    // Clear user data
    fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_USER_NAME)
        editor.remove(KEY_USER_EMAIL)
        editor.remove(KEY_USER_PASSWORD)
        editor.remove(KEY_USER_PHONE)
        editor.remove(KEY_USER_ADDRESS)
        editor.remove(KEY_USER_IMAGE)
        editor.remove(KEY_USER_LOC)
        editor.remove(KEY_TOKEN)
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "UserData"
        private const val KEY_USER_ID = "_id"
        private const val KEY_USER_NAME = "name"
        private const val KEY_USER_EMAIL = "email"
        private const val KEY_USER_PHONE = "phone"
        private const val KEY_USER_ADDRESS = "address"
        private const val KEY_USER_PASSWORD = "password"
        private const val KEY_USER_IMAGE = "image"
        private const val KEY_USER_LOC = "loc"
        private const val KEY_TOKEN = "token"

        //
        private const val KEY_SHOP_ADDRESS = "shop_address"
        private const val KEY_SHOP_LONG = "shop_longitude"
        private const val KEY_SHOP_LAT = "shop_latitude"
    }

}
