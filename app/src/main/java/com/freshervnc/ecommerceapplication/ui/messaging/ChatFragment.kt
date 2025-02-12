package com.freshervnc.ecommerceapplication.ui.messaging

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.databinding.FragmentChatBinding
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.Date

class ChatFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentChatBinding
    private val viewModel by activityViewModels<ChatViewModel>()
    private lateinit var preferencesUtils : PreferencesUtils
    var userId : String = ""
    var token : String = ""
    companion object {
        const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 100
        private const val REQUEST_CODE_CAMERA = 101
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        preferencesUtils = PreferencesUtils(requireContext())
        userId = arguments?.getString("userId") ?: ""
        viewModel.getUserInfo(GetUserInfoRequest(userId))
    }

    override fun setView() {
    }

    override fun setAction() {
        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.iconSend.setOnClickListener {
            val date = Date()
            viewModel.sendMessage(
                messageTxt = binding.edChating.text.toString(),
                senderUid = preferencesUtils.userId.toString(),
                date = date.time,
                senderRoom = preferencesUtils.userId.toString() + userId,
                receiverRoom = userId + preferencesUtils.userId.toString(),
            )
        }
    }

    override fun setObserve() {
        viewModel.getUserInfoResult().observe(viewLifecycleOwner, Observer {
            getUserInfoResult(it)
        })
        viewModel.getSuccessful().observe(viewLifecycleOwner, Observer { result ->
            when(result){
                true -> {
                    viewModel.pushNotification(
                        PushNotificationRequest(
                            registrationToken = preferencesUtils.token,
                            title = preferencesUtils.userName,
                            body = binding.edChating.text.toString()
                        )
                    )
                }
                false -> {

                }
            }
        })
    }

    private fun getUserInfoResult(event: Event<Resource<GetUserInfoResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when(response){
                is Resource.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data?.let {
                        binding.tvTopChat.text = it.user?.name
                        Glide.with(requireContext()).load(it.user?.image).into(binding.chatProfile)
                        token = it.user?.token ?: ""
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