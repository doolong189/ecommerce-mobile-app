package com.freshervnc.ecommerceapplication.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.DetailOrderAdapter
import com.freshervnc.ecommerceapplication.adapter.OrderAdapter.Companion.cancelStatus
import com.freshervnc.ecommerceapplication.adapter.OrderAdapter.Companion.completedStatus
import com.freshervnc.ecommerceapplication.adapter.OrderAdapter.Companion.processStatus
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetDetailOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailOrderResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentDetailOrderBinding
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource


class DetailOrderFragment : BaseFragment() {
    private lateinit var binding : FragmentDetailOrderBinding
    override var isVisibleActionBar: Boolean = false
    private val viewModel by activityViewModels<DetailOrderViewModel>()
    private var detailOrderAdapter = DetailOrderAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        binding.rcv.layoutManager = LinearLayoutManager(requireContext())
        binding.rcv.run { adapter = DetailOrderAdapter().also { detailOrderAdapter = it } }

        val orderId = arguments?.getString("orderId") ?: ""
        viewModel.getDetailOrder(GetDetailOrderRequest(id = orderId))
    }

    override fun setView() {}

    override fun setAction() {}

    override fun setObserve() {
        viewModel.getDetailOrderResult().observe(viewLifecycleOwner, Observer {
            getDetailOrderResult(it)
        })
    }

    private fun getDetailOrderResult(event : Event<Resource<GetDetailOrderResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response){
                is Resource.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data?.let {
                        detailOrderAdapter.submitList(it.data?.products!!)
                        binding.deliveryCode.text = "Mã vận chuyển: "+it.data?._id
                        if (it.data.receiptStatus == 0) {
                            binding.deliveryStatus.setTextColor(binding.root.resources.getColor(R.color.text))
                            binding.deliveryStatus.text = "Trạng thái: $processStatus"
                        }else if(it.data.receiptStatus == 1){
                            binding.deliveryStatus.setTextColor(binding.root.resources.getColor(R.color.completed))
                            binding.deliveryStatus.text = "Trạng thái: $completedStatus"
                        }else{
                            binding.deliveryStatus.setTextColor(binding.root.resources.getColor(R.color.cancel))
                            binding.deliveryStatus.text = "Trạng thái: $cancelStatus"
                        }
                        binding.deliveryDate.text = "Thời gian nhận giao hàng: "+it.data?.date
                        binding.receiveInfo.text = it.data?.idClient?.name + " | " + it.data?.idClient?.phone
                        binding.deliveryAddress.text = it.data.idClient?.address

                        Log.e("zzzz","${it.data.idClient?.phone}")
                    }
                }
                is Resource.Loading -> {
                    binding.pgBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.pgBar.visibility = View.GONE
                }
            }
        }
    }
}