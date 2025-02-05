package com.freshervnc.ecommerceapplication.ui.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.NotificationAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.AddNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.AddNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentNotificationBinding
import com.freshervnc.ecommerceapplication.ui.message.MessageViewModel
import com.freshervnc.ecommerceapplication.utils.Contacts
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource


class NotificationFragment : BaseFragment() {

    override var isVisibleActionBar: Boolean = true
    private lateinit var binding : FragmentNotificationBinding
    private val viewModel by activityViewModels<NotificationViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var notificationAdapter = NotificationAdapter()
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())

    }

    override fun setView() {
        Log.e(Contacts.TAG,"${preferences.token}")
        viewModel.pushNotification(PushNotificationRequest(
            registrationToken = preferences.token,
            title = "Test Push Title ${count+1}",
            body = "Test Push Body ${count+1}"
        ))
        binding.rcNotification.layoutManager = LinearLayoutManager(requireContext())
        binding.rcNotification.run { adapter = NotificationAdapter().also { notificationAdapter = it }}
        viewModel.getNotification(GetNotificationRequest(id = preferences.userId.toString()))
    }

    override fun setAction() {
    }

    override fun setObserve() {
        viewModel.pushNotificationResult().observe(viewLifecycleOwner, Observer {
            pushNotificationResult(it)
        })

        viewModel.addNotificationResult().observe(viewLifecycleOwner, Observer {
            addNotificationResult(it)
        })

        viewModel.getNotificationResult().observe(viewLifecycleOwner, Observer {
            getNotificationResult(it)
        })
    }

    private fun pushNotificationResult(event: Event<Resource<PushNotificationResponse>>) {
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.notificationPbBar.visibility = View.GONE
                    response.data.let {
                        viewModel.addNotification(AddNotificationRequest(
                            title = it?.notification?.title,
                            body = it?.notification?.body,
                            idUser = preferences.userId
                        ))
                    }
                }
                is Resource.Loading -> {
                    binding.notificationPbBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.notificationPbBar.visibility = View.GONE
                }
            }

        }
    }

    private fun addNotificationResult(event: Event<Resource<AddNotificationResponse>>) {
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.notificationPbBar.visibility = View.GONE

                }
                is Resource.Loading -> {
                    binding.notificationPbBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.notificationPbBar.visibility = View.GONE
                }
            }
        }
    }

    private fun getNotificationResult(event: Event<Resource<GetNotificationResponse>>) {
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.notificationPbBar.visibility = View.GONE
                    response.data?.let {
                        notificationAdapter.submitList(it.notifications!!)
                    }
                }

                is Resource.Loading -> {
                    binding.notificationPbBar.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    binding.notificationPbBar.visibility = View.GONE
                }
            }
        }
    }
}