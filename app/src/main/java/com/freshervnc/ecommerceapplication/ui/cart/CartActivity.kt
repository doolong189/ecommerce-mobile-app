package com.freshervnc.ecommerceapplication.ui.cart

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.base.BaseActivity

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setupActionBarWithNavController(navHostFragment.navController)
    }
}