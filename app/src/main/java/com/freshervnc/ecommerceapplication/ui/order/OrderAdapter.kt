package com.freshervnc.ecommerceapplication.ui.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.freshervnc.ecommerceapplication.ui.order.tabLayout.CancelOrderFragment
import com.freshervnc.ecommerceapplication.ui.order.tabLayout.CompletedOrderFragment
import com.freshervnc.ecommerceapplication.ui.order.tabLayout.ProcessOrderFragment

class OrderAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProcessOrderFragment()
            1 -> CompletedOrderFragment()
            2 -> CancelOrderFragment()
            else -> ProcessOrderFragment()
        }
    }
}