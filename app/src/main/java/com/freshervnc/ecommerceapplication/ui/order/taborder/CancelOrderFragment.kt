package com.freshervnc.ecommerceapplication.ui.order.taborder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.adapter.OrderAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetOrderResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentCancelOrderBinding
import com.freshervnc.ecommerceapplication.ui.order.OrderViewModel
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource


class CancelOrderFragment : BaseFragment() {

    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentCancelOrderBinding
    private val viewModel by activityViewModels<OrderViewModel>()
    private var orderAdapter = OrderAdapter()
    private lateinit var preferences : PreferencesUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCancelOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())
        binding.cancelOrderRcView.layoutManager = LinearLayoutManager(requireContext())
        binding.cancelOrderRcView.run { adapter = OrderAdapter().also { orderAdapter = it } }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrder(GetOrderRequest(id = preferences.userId , receiptStatus = 2))
    }
    override fun setView() {
    }

    override fun setAction() {
    }

    override fun setObserve() {
        viewModel.getCancelOrderResult().observe(viewLifecycleOwner, Observer {
            getOrderResult(it)
        })
    }
    private fun getOrderResult(event : Event<Resource<GetOrderResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.cancelOrderPbBar.visibility = View.GONE
                    response.data?.let {
                        orderAdapter.submitList(it.data!!)
                    }
                }
                is Resource.Loading -> {
                    binding.cancelOrderPbBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.cancelOrderPbBar.visibility = View.GONE
                    Toast.makeText(requireContext(),response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}