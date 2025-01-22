package com.freshervnc.ecommerceapplication.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ActivityMainBinding
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingFragment
import com.freshervnc.ecommerceapplication.ui.message.MessageActivity
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
                    ((supportFragmentManager.fragments[0] as NavHostFragment).childFragmentManager.fragments[0] as? ShoppingFragment)
                }
                R.id.navMessenger ->{
                    startActivity(Intent(this,MessageActivity::class.java))
                    finish()
                }
                R.id.navFindMap -> {

                }
                R.id.navNotification -> {
                    startActivity(Intent(this,NotificationActivity::class.java))
                    finish()
                }
                R.id.navUser -> {
                    startActivity(Intent(this,UserActivity::class.java))
                    finish()
                }
                else -> {}
            }
            false
        }

    }
}