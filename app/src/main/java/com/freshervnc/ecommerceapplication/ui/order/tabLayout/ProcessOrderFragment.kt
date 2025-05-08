package com.freshervnc.ecommerceapplication.ui.order.tabLayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.OrderAdapter
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetOrderResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentProcessOrderBinding
import com.freshervnc.ecommerceapplication.ui.order.OrderViewModel
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource


class ProcessOrderFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentProcessOrderBinding
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
        binding  = FragmentProcessOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())
        binding.processOrderRcView.layoutManager = LinearLayoutManager(requireContext())
        binding.processOrderRcView.run { adapter = OrderAdapter().also { orderAdapter = it } }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrder(GetOrderRequest(id = preferences.userId , receiptStatus = 0))
    }

    override fun setView() {}

    override fun setAction() {
        orderAdapter.onClickItemOrder { id, position ->
            val bundle = Bundle().apply { putString("orderId", id._id) }
            findNavController().navigate(R.id.action_orderFragment2_to_detailOrderFragment2,bundle)
        }
    }

    override fun setObserve() {
        viewModel.getCompressOrderResult().observe(viewLifecycleOwner, Observer {
            getOrderResult(it)
        })
    }

    private fun getOrderResult(event : Event<Resource<GetOrderResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.processOrderPbBar.visibility = View.GONE
                    response.data?.let {
                        orderAdapter.submitList(it.data!!)
                    }
                }
                is Resource.Loading -> {
                    binding.processOrderPbBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.processOrderPbBar.visibility = View.GONE
                    Toast.makeText(requireContext(),response.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}