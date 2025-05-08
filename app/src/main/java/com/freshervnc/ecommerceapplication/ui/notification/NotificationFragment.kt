package com.freshervnc.ecommerceapplication.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.NotificationAdapter
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.CreateNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetNotificationResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentNotificationBinding
import com.freshervnc.ecommerceapplication.utils.Constants
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.SwipeHelper


class NotificationFragment : BaseFragment() {

    override var isVisibleActionBar: Boolean = true
    private lateinit var binding : FragmentNotificationBinding
    private val viewModel by activityViewModels<NotificationViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var notificationAdapter = NotificationAdapter()
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
        Log.e(Constants.TAG,"${preferences.token}")
        binding.rcNotification.layoutManager = LinearLayoutManager(requireContext())
        binding.rcNotification.run { adapter = NotificationAdapter().also { notificationAdapter = it }}
        viewModel.getNotification(GetNotificationRequest(id = preferences.userId.toString()))
    }

    override fun setView() {
    }

    override fun setAction() {
        notificationAdapter.onClickItemNotification { id, position ->
            val bundle = Bundle().apply { putString("notificationId", id._id) }
            findNavController().navigate(R.id.action_notificationFragment_to_detailNotificationFragment , bundle)
        }

        object : SwipeHelper(requireContext(), binding.rcNotification) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: MutableList<UnderlayButton>?
            ) {
                underlayButtons?.add(
                    UnderlayButton(
                        getString(R.string.delete),
                        0,
                        ContextCompat.getColor(requireContext(), R.color.def)
                    ) { position ->

                    }
                )
            }
        }
    }

    override fun setObserve() {
        viewModel.pushNotificationResult().observe(viewLifecycleOwner, Observer {
            pushNotificationResult(it)
        })

        viewModel.createNotificationResult().observe(viewLifecycleOwner, Observer {
            createNotificationResult(it)
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
                        viewModel.createNotification(
                            CreateNotificationRequest(
                                title = it?.notification?.title,
                                body = it?.notification?.body,
                                image = it?.notification?.image,
                                idUser = preferences.userId,
                                type = it?.notification?.type
                            )
                        )
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

    private fun createNotificationResult(event: Event<Resource<CreateNotificationResponse>>) {
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
                    binding.notificationTvEmptyNtf.visibility = View.VISIBLE
                    binding.notificationTvEmptyNtf.text = response.message
                }
            }
        }
    }
}