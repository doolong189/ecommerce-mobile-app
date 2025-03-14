package com.freshervnc.ecommerceapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ActivityMainBinding
import com.freshervnc.ecommerceapplication.ui.mapbox.LocationActivity
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingFragment
import com.freshervnc.ecommerceapplication.ui.messaging.MessageActivity
import com.freshervnc.ecommerceapplication.ui.notification.NotificationActivity
import com.freshervnc.ecommerceapplication.ui.user.UserActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView(savedInstanceState != null)
        setAction()
    }

    private fun setView(isRestore: Boolean){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setupActionBarWithNavController(navHostFragment.navController)

        if (isRestore) {
            val fragments = supportFragmentManager.fragments
            if (fragments.isNotEmpty() && (fragments[0] is ShoppingFragment).not()) {
                binding.mainBottomNav.selectedItemId = R.id.navShopping
            }
        }
    }

    private fun setAction(){
        binding.mainBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navShopping -> {
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.shoppingFragment)
                }
                R.id.navMessenger ->{
                    startActivity(Intent(this,MessageActivity::class.java))
                }
                R.id.navFindMap -> {
//                    startActivity(Intent(this,MapEcommerceActivity::class.java))
                    startActivity(Intent(this, LocationActivity::class.java))
                }
                R.id.navNotification -> {
                    startActivity(Intent(this,NotificationActivity::class.java))
                }
                R.id.navUser -> {
                    startActivity(Intent(this,UserActivity::class.java))
                }
                else -> {}
            }
            false
        }

    }

    fun GONE(){
        binding.mainBottomNav.visibility = View.GONE
    }

    fun Visiable(){
        binding.mainBottomNav.visibility = View.VISIBLE
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }
}