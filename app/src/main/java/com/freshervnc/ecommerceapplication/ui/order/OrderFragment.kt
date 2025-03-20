package com.freshervnc.ecommerceapplication.ui.order

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.databinding.FragmentOrderBinding
import com.google.android.material.tabs.TabLayoutMediator

class OrderFragment : BaseFragment() {
    private lateinit var binding : FragmentOrderBinding

    override var isVisibleActionBar: Boolean = false
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {

    }

    override fun setView() {
        binding.orderViewPager2.isUserInputEnabled = false
        binding.orderViewPager2.adapter = OrderAdapter(requireActivity())

        tabLayoutMediator = TabLayoutMediator(binding.orderTabLayout, binding.orderViewPager2) { tab, position ->
            // Tạo một TextView tùy chỉnh cho mỗi tab
            val tabTextView = TextView(requireContext()).apply {
                text = when (position) {
                    0 -> getString(R.string.process_order)
                    1 -> getString(R.string.completed_order)
                    2 -> getString(R.string.cancel_order)
                    else -> ""
                }
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.defaultColor))
                gravity = Gravity.CENTER
            }
            tab.customView = tabTextView
        }
        tabLayoutMediator.attach()
    }

    override fun setAction() {
    }

    override fun setObserve() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

}