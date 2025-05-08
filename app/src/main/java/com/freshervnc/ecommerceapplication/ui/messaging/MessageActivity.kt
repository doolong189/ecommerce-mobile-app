package com.freshervnc.ecommerceapplication.ui.messaging

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.base.BaseActivity
import com.freshervnc.ecommerceapplication.databinding.ActivityMessageBinding
import com.freshervnc.ecommerceapplication.ui.findmap.MapEcommerceActivity

class MessageActivity : BaseActivity() {
    private lateinit var binding : ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setupActionBarWithNavController(navHostFragment.navController)
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_messaging)

        navController.graph = navGraph.apply {
            val moveTo = intent.getStringExtra("move_to")
            val idUser = intent.getStringExtra("userId")
            if (moveTo == MapEcommerceActivity.MoveTo.Map.value) {
                val bundle = Bundle().apply { putString("userId", idUser) }
                ((supportFragmentManager.fragments[0] as NavHostFragment).childFragmentManager.fragments[0] as? MessageFragment)?.let {
                    it.findNavController().navigate(R.id.action_messageFragment_to_chatFragment, bundle)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}