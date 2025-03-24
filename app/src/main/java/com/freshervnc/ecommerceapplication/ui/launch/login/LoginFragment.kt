package com.freshervnc.ecommerceapplication.ui.launch.login

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.enity.LoginResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentLoginBinding
import com.freshervnc.ecommerceapplication.ui.main.MainActivity
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.google.firebase.messaging.FirebaseMessaging


class LoginFragment : BaseFragment() {
    private lateinit var binding : FragmentLoginBinding
    private val viewModel by activityViewModels<LoginViewModel>()
    override var isVisibleActionBar: Boolean = false
    private lateinit var preferences : PreferencesUtils
    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())
    }


    override fun setView() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            token = task.result
            Log.d("FCM", "FCM Token: $token")
        }
    }

    override fun setAction() {
        preferences.clearUserData()
        binding.btnLogin.setOnClickListener {
            if(binding.edEmail.text.toString().isEmpty() || binding.edPassword.text.toString().isEmpty()){
                viewModel.getLogin(LoginRequest(email = "test3", password = "123"))
            }
            viewModel.getLogin(LoginRequest(email = binding.edEmail.text.toString(), password = binding.edPassword.text.toString()))
        }
        binding.tvRegister.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            findNavController().navigate(R.id.action_loginFragment_to_registerAccountFragment)
        }
    }

    override fun setObserve() {
        viewModel.getLoginResult().observe(viewLifecycleOwner, Observer {
            getLoginResult(it)
        })
    }

    private fun getLoginResult(event: Event<Resource<LoginResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    preferences.saveUserData(user = response.data?.user,token = token)
                    startActivity(Intent(requireContext(),MainActivity::class.java))
                    requireActivity().finish()
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