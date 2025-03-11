package com.freshervnc.ecommerceapplication.ui.messaging

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.ChatAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageResponse
import com.freshervnc.ecommerceapplication.data.enity.GetChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetChatMessageResponse
import com.freshervnc.ecommerceapplication.data.enity.GetMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetNeedTokenRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.databinding.FragmentChatBinding
import com.freshervnc.ecommerceapplication.model.Chat
import com.freshervnc.ecommerceapplication.model.Message
import com.freshervnc.ecommerceapplication.ui.notification.NotificationViewModel
import com.freshervnc.ecommerceapplication.ui.user.UserViewModel
import com.freshervnc.ecommerceapplication.utils.Contacts
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.Date
import java.util.UUID

class ChatFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentChatBinding
//    private val viewModel by activityViewModels<ChatViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private val messageViewModel by activityViewModels<MessageViewModel>()
    private val notificationViewModel by activityViewModels<NotificationViewModel>()
    private lateinit var preferencesUtils : PreferencesUtils
    private lateinit var chatAdapter : ChatAdapter
    var receiverId : String = ""
    var messageId : String = ""
    var token : String = ""
    private lateinit var webSocket: WebSocket
    private lateinit var viewModel: ChatViewModel

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
        receiverId = arguments?.getString("userId") ?: ""
        messageId = arguments?.getString("messageId") ?: ""
        binding.rcChat.setHasFixedSize(true)
        binding.rcChat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcChat.run {adapter = ChatAdapter(requireContext()).also { chatAdapter = it }}

        userViewModel.getUserInfo(GetUserInfoRequest(receiverId))
//        viewModel.getMessages(GetMessageRequest((userId + preferencesUtils.userId)))
        userViewModel.getNeedToken(GetNeedTokenRequest(id = receiverId , token = preferencesUtils.token.toString()))
        messageViewModel.getChatMessage(GetChatMessageRequest(messageId))

        //chat - web socket
        val client = OkHttpClient()
        val request = Request.Builder().url("${Contacts.URL_WEBSOCKET}=${messageId}").build()
//        webSocket = client.newWebSocket(request, object : WebSocketListener() {
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                requireActivity().runOnUiThread {
//                    val messageText = binding.edChating.text.toString()
//                    viewModel.addMessage(Chat(
//                        messageImage = "",
//                        messageText = messageText,
//                        senderId = preferencesUtils.userId.toString(),
//                        timestamp = System.currentTimeMillis()
//                    ))
//                    viewModel.createMessage(CreateChatMessageRequest(
//                        messageId = preferencesUtils.userId + receiverId,
//                        messageImage = "",
//                        messageText = messageText,
//                        senderId = preferencesUtils.userId,
//                        receiverId = receiverId,
//                        senderChatId = preferencesUtils.userId,
//                        timestamp = System.currentTimeMillis()
//                    ))
//
//                    viewModel.createMessage(CreateChatMessageRequest(
//                        messageId = receiverId + preferencesUtils.userId,
//                        messageImage = "",
//                        messageText = messageText,
//                        senderId = receiverId,
//                        receiverId = preferencesUtils.userId,
//                        senderChatId = preferencesUtils.userId,
//                        timestamp = System.currentTimeMillis()
//                    ))
//                }
//            }
//        })

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        // Gửi tin nhắn
        binding.iconSend.setOnClickListener {
            val message = binding.edChating.text.toString()
            val token = "your_token_here"  // Lấy token từ SharedPreferences hoặc session
            viewModel.sendMessage("",preferencesUtils.userId.toString(), message, Util)
        }

        // Lấy tin nhắn khi fragment được tạo
//        val token = "your_token_here"  // Lấy token từ SharedPreferences hoặc session
//        viewModel.getMessages(token)

        // Lắng nghe tin nhắn mới qua socket
        viewModel.listenForMessages()

        // Lắng nghe lỗi qua socket
        viewModel.listenForErrors()
    }

    override fun setView() {

    }

    override fun setAction() {
        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.iconSend.setOnClickListener {
//            val messageText = binding.edChating.text.toString()
//            webSocket.send(messageText)
//            viewModel.addMessage(Chat(
//                messageImage = "",
//                messageText = messageText,
//                senderId = preferencesUtils.userId.toString(),
//                timestamp = System.currentTimeMillis()
//            ))
//            binding.edChating.text.clear()

            val message = binding.edChating.text.toString()
            viewModel.sendMessage(token, message)
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

        })

        viewModel.chats.observe(this) { messages ->
            chatAdapter.submitList(messages)
            binding.rcChat.scrollToPosition(messages.size - 1)
        }

        // Quan sát các LiveData
        viewModel.messages.observe(viewLifecycleOwner, Observer { messages ->
            chatAdapter.submitList(messages)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
//edit
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
                        Log.e("zzzzz","${it}")
                        chatAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun createMessageResult(event: Event<Resource<CreateChatMessageResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when ( response ){
                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    notificationViewModel.pushNotification(PushNotificationRequest(registrationToken = preferencesUtils.token, title = preferencesUtils.userName, body = binding.edChating.text.toString()))
                }
            }
        }
    }
}