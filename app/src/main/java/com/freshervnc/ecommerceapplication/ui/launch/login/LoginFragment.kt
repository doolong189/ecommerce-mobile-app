package com.freshervnc.ecommerceapplication.ui.launch.login

import android.content.Intent
import android.os.Bundle
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
import com.freshervnc.ecommerceapplication.utils.Resource


class LoginFragment : BaseFragment() {
    private lateinit var binding : FragmentLoginBinding
    private val viewModel by activityViewModels<LoginViewModel>()
    override var isVisibleActionBar: Boolean = false
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
    }

    override fun setView() {
    }

    override fun setAction() {
        binding.btnLogin.setOnClickListener {
            viewModel.getLogin(LoginRequest(email = "test3", password = "123"))
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