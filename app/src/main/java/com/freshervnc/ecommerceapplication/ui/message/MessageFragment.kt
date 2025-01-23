package com.freshervnc.ecommerceapplication.ui.message

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.CategoryAdapter
import com.freshervnc.ecommerceapplication.adapter.MessageAdapter
import com.freshervnc.ecommerceapplication.adapter.UserAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.enity.GetMessageRequest
import com.freshervnc.ecommerceapplication.databinding.FragmentMessageBinding
import com.freshervnc.ecommerceapplication.ui.launch.login.LoginViewModel
import com.freshervnc.ecommerceapplication.ui.main.MainActivity
import com.freshervnc.ecommerceapplication.utils.Contacts
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
    private lateinit var messageAdapter: MessageAdapter
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
        binding.rcUser.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rcUser.run { adapter = UserAdapter().also { userAdapter = it } }

        viewModel.getUsers(GetAllUserRequest(id = preferences.userId))
    }

    override fun setAction() {
    }

    override fun setObserve() {
        viewModel.getUsersResult().observe(viewLifecycleOwner, Observer {
            getUsersResult(it)
        })

    }

    private fun getUsersResult(event: Event<Resource<GetAllUserResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    response.data?.let {
                        userAdapter.submitList(it.users!!)

                        val senderId = preferences.userId!!
                        for (item in it.users!!){
                            senderRoom = senderId + item._id
                        }
                        messageAdapter = MessageAdapter()
                        binding.rcHistoryChat.adapter = messageAdapter
                        viewModel.fetchHistoryMessage(it.users!!)
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
}