package com.freshervnc.ecommerceapplication.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetDetailNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailNotificationResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentDetailNotificationBinding
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource

class DetailNotificationFragment : BaseFragment() {
    private lateinit var binding : FragmentDetailNotificationBinding
    private val viewModel by activityViewModels<DetailNotificationViewModel>()
    override var isVisibleActionBar: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun initView() {
        val notificationId = arguments?.getString("notificationId") ?: ""
        viewModel.getDetailNotification(GetDetailNotificationRequest(id = notificationId))
    }

    override fun setView() {
    }

    override fun setAction() {
    }

    override fun setObserve() {
        viewModel.getDetailNotificationResult().observe(viewLifecycleOwner, Observer {
            getDetailNotificationResult(it)
        })
    }

    private fun getDetailNotificationResult(event: Event<Resource<GetDetailNotificationResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response){
                is Resource.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data?.let {
                        binding.tvTitle.text = it.notification?.title
                        binding.tvBody.text = it.notification?.body
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