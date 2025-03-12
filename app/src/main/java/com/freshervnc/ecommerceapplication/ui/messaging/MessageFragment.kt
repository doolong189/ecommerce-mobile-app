package com.freshervnc.ecommerceapplication.ui.messaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.HistoryMessageAdapter
import com.freshervnc.ecommerceapplication.adapter.UserAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.enity.GetHistoryChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetHistoryChatMessageResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentMessageBinding
import com.freshervnc.ecommerceapplication.model.UserInfo
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.google.firebase.FirebaseApp


class MessageFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = true
    private lateinit var binding : FragmentMessageBinding
    private val viewModel by activityViewModels<MessageViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var userAdapter = UserAdapter()
    private var historyMessageAdapter = HistoryMessageAdapter()
    var senderRoom = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(layoutInflater, container, false)
        FirebaseApp.initializeApp(requireContext())
        return binding.root
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())
    }

    override fun setView() {
        binding.rcUser.setHasFixedSize(true)
        binding.rcUser.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rcUser.run { adapter = UserAdapter().also { userAdapter = it } }


        binding.rcHistoryChat.setHasFixedSize(true)
        binding.rcHistoryChat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcHistoryChat.run {adapter = HistoryMessageAdapter().also { historyMessageAdapter = it }}


//        viewModel.getUsers(GetAllUserRequest(id = preferences.userId))
        viewModel.getHistoryChatMessages(GetHistoryChatMessageRequest( senderId = preferences.userId))
    }

    override fun setAction() {
        historyMessageAdapter.onClickItemMessage{ id, position ->
            val bundle = Bundle().apply {
                putString("userId", id.receiverId._id)
                putString("messageId" , id.messageId)
            }
            findNavController().navigate(R.id.action_messageFragment_to_chatFragment , bundle)
        }
    }

    override fun setObserve() {
        viewModel.getHistoryChatMessagesResult().observe(viewLifecycleOwner, Observer {
            getHistoryChatMessagesResult(it)
        })
    }

    private fun getUsersResult(event: Event<Resource<GetAllUserResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    response.data?.let {
                        userAdapter.submitList(it.users!!)
                    }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    response.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getHistoryChatMessagesResult(event: Event<Resource<GetHistoryChatMessageResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when ( response ){
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE

                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    //xu ly check null
                    response.data?.let {
                        historyMessageAdapter.setMessage(it.messages!!)
                        val list = mutableListOf<UserInfo>()
                        it.messages.map { item ->
                            list.add(item.receiverId)
                        }
                        userAdapter.submitList(list)
                    }
                }
            }
        }
    }
}