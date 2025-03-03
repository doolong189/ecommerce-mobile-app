package com.freshervnc.ecommerceapplication.ui.messaging

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.ChatAdapter
import com.freshervnc.ecommerceapplication.adapter.MessageAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetMessageResponse
import com.freshervnc.ecommerceapplication.data.enity.GetNeedTokenRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.databinding.FragmentChatBinding
import com.freshervnc.ecommerceapplication.model.Message
import com.freshervnc.ecommerceapplication.ui.user.UserViewModel
import com.freshervnc.ecommerceapplication.utils.Contacts
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import kotlinx.coroutines.Job
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
    private val viewModel by activityViewModels<ChatViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private lateinit var preferencesUtils : PreferencesUtils
    private lateinit var chatAdapter : ChatAdapter
    var userId : String = ""
    var token : String = ""
    private lateinit var webSocket: WebSocket
    private val messages = mutableListOf<Message>()

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

        binding.rcChat.setHasFixedSize(true)
        binding.rcChat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcChat.run {adapter = ChatAdapter(requireContext()).also { chatAdapter = it }}

        viewModel.getUserInfo(GetUserInfoRequest(userId))
        viewModel.getMessages(GetMessageRequest((userId + preferencesUtils.userId)))
        userViewModel.getNeedToken(GetNeedTokenRequest(id = userId , token = preferencesUtils.token.toString()))


        //chat - web socket
        val client = OkHttpClient()
        val request = Request.Builder().url("${Contacts.URL_WEBSOCKET}=${userId}").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onMessage(webSocket: WebSocket, text: String) {
                lifecycleScope.launch {
                    val newMessage = Message(
                        messageText = text,
                        senderId = "server",
                        timestamp = System.currentTimeMillis()
                    )
                    messages.add(newMessage)
//                    messageAdapter.addMessage(Message(text, false))
                    binding.rcChat.scrollToPosition(messages.size - 1)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                lifecycleScope.launch {
                // Xử lý lỗi WebSocket nếu cần
                }
            }
        })
    }

    override fun setView() {}

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
        userViewModel.getNeedTokenResult().observe(viewLifecycleOwner , Observer {

        })
        viewModel.getSuccessful().observe(viewLifecycleOwner, Observer { result ->
            when(result){
                true -> {
                    viewModel.pushNotification(PushNotificationRequest(registrationToken = preferencesUtils.token, title = preferencesUtils.userName, body = binding.edChating.text.toString()))
                    binding.edChating.setText("")
                }
                false -> {
                    Toast.makeText(requireContext(), getString(R.string.error_send_message), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getMessageResult().observe(viewLifecycleOwner , Observer {
            it.messages?.let { response -> chatAdapter.submitList(response) }
        })

        viewModel.getErrorMessageResult().observe(viewLifecycleOwner , Observer {
            Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
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