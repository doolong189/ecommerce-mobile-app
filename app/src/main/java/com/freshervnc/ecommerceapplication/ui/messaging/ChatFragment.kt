package com.freshervnc.ecommerceapplication.ui.messaging

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.adapter.ChatAdapter
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageResponse
import com.freshervnc.ecommerceapplication.data.enity.CreateNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.GetChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetChatMessageResponse
import com.freshervnc.ecommerceapplication.data.enity.GetNeedTokenRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentChatBinding
import com.freshervnc.ecommerceapplication.data.model.Chat
import com.freshervnc.ecommerceapplication.ui.notification.NotificationViewModel
import com.freshervnc.ecommerceapplication.ui.user.UserViewModel
import com.freshervnc.ecommerceapplication.utils.Constants
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject

class ChatFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentChatBinding
    private val viewModel by activityViewModels<ChatViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private val messageViewModel by activityViewModels<MessageViewModel>()
    private val notificationViewModel by activityViewModels<NotificationViewModel>()
    private lateinit var preferencesUtils : PreferencesUtils
    private lateinit var chatAdapter : ChatAdapter
    var receiverId : String = ""
    var senderId : String = ""
    var messageId : String = ""
    var token : String = ""
    private var socket: Socket? = null
    var chatList: ArrayList<Chat> = arrayListOf()
    var message : String = ""

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
        senderId = preferencesUtils.userId ?: ""
        receiverId = arguments?.getString("userId") ?: ""
        messageId = arguments?.getString("messageId") ?: ""
        binding.rcChat.setHasFixedSize(true)
        binding.rcChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rcChat.setItemAnimator(DefaultItemAnimator())
        binding.rcChat.run {adapter = ChatAdapter(requireContext()).also { chatAdapter = it }}

        userViewModel.getUserInfo(GetUserInfoRequest(receiverId))
        userViewModel.getNeedToken(GetNeedTokenRequest(id = preferencesUtils.userId.toString() , token = preferencesUtils.token.toString()))
        messageViewModel.getChatMessage(GetChatMessageRequest(messageId))
        chatSocket()
    }

    override fun setView() {}

    override fun setAction() {
        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun chatSocket(){
        try {
            socket = IO.socket("${Constants.BASE_URL_SOCKET}?userId=${senderId}&partnerId=${receiverId}")
            socket?.connect()
            Log.e(Constants.TAG,"socket connect")
        } catch (e : Exception) {
            e.printStackTrace()
        }

        binding.iconSend.setOnClickListener {
            if(binding.edChating.text.toString().isNotEmpty()){
                socket?.emit("message",binding.edChating.text.toString(), senderId)
                notificationViewModel.pushNotification(
                    PushNotificationRequest(
                        registrationToken = token,
                        title = preferencesUtils.userName,
                        body = binding.edChating.text.toString(),
                        image = viewModel.imageUrl,
                        type = TYPE_NOTIFICATION
                    )
                )
                viewModel.createMessage(
                    CreateChatMessageRequest(
                        messageId = senderId + receiverId,
                        messageImage = "",
                        messageText = binding.edChating.text.toString(),
                        senderId = senderId,
                        receiverId = receiverId,
                        senderChatId = senderId,
                        timestamp = System.currentTimeMillis()
                    )
                )
                binding.edChating.setText("")
            }
        }

        socket?.on("message") { args ->
            requireActivity().runOnUiThread {
                val data = args[0] as JSONObject
                try {
                    val username = data.getString("id")
                    message = data.getString("message")
                    val m = Chat(
                        messageImage = "",
                        messageText = message,
                        senderId = username,
                        timestamp = System.currentTimeMillis())
                    chatList.add(m)
                    chatAdapter.submitList(chatList)
                    binding.rcChat.scrollToPosition(chatList.size - 1)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        socket?.on("close") { args ->
            requireActivity().runOnUiThread {
                val data = args[0] as String
                Toast.makeText(requireContext(), data, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun setObserve() {
        userViewModel.getUserInfoResult().observe(viewLifecycleOwner, Observer {
            getUserInfoResult(it)
        })
        userViewModel.getNeedTokenResult().observe(viewLifecycleOwner , Observer {
        })
        messageViewModel.getChatMessageResult().observe(viewLifecycleOwner , Observer{
            getChatMessageResult(it)
        })

        viewModel.createMessageResult().observe(viewLifecycleOwner, Observer{
            createMessageResult(it)
        })
        notificationViewModel.pushNotificationResult().observe(viewLifecycleOwner , Observer{
            pushNotificationResult(it)
        })
//        viewModel.chats.observe(this) { messages ->
//            chatAdapter.submitList(messages)
//            binding.rcChat.scrollToPosition(messages.size - 1)
//        }
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

    private fun getChatMessageResult(event: Event<Resource<GetChatMessageResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when ( response ){
                is Resource.Error -> {
                    binding.pgBar.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.pgBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data?.messages?.chats?.let {
                        chatList.addAll(it)
                        chatAdapter.submitList(it)
                        binding.rcChat.scrollToPosition(it.size - 1)
                    }
                }
            }
        }
    }

    private fun createMessageResult(event: Event<Resource<CreateChatMessageResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when ( response ){
                is Resource.Error -> {
                   Log.e(Constants.TAG,"${response.message}")
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    chatAdapter.submitList(chatList)
                    binding.rcChat.scrollToPosition(chatList.size - 1)
                }
            }
        }
    }

    private fun pushNotificationResult(event: Event<Resource<PushNotificationResponse>>) {
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.let {
                        notificationViewModel.createNotification(
                            CreateNotificationRequest(
                                title = it?.notification?.title,
                                body = it?.notification?.body,
                                image = it?.notification?.image,
                                idUser = preferencesUtils.userId,
                                type = it?.notification?.type
                            )
                        )
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                }
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        socket?.disconnect()
    }

    companion object{
        val TYPE_NOTIFICATION = "3"
    }
}