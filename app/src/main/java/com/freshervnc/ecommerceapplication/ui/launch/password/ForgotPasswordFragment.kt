package com.freshervnc.ecommerceapplication.ui.launch.password

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GenerateOtpRequest
import com.freshervnc.ecommerceapplication.data.enity.GenerateOtpResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentForgotPasswordBinding
import com.freshervnc.ecommerceapplication.ui.main.MainActivity
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.SocketIOManager


class ForgotPasswordFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentForgotPasswordBinding
    private val viewModel by activityViewModels<PasswordViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
    }

    override fun setView() {
    }

    override fun setAction() {
        binding.btnSendOtp.setOnClickListener {
            viewModel.generateOtp(
                GenerateOtpRequest(
                    toEmail = binding.edEmail.text.toString()
                )
            )
        }
    }

    override fun setObserve() {
        viewModel.generateOtpResult().observe(viewLifecycleOwner, Observer {
            generateOtpResult(it)
        })
    }

    private fun generateOtpResult(event: Event<Resource<GenerateOtpResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val bundle = Bundle().apply { putString("email", binding.edEmail.text.toString()) }

                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_verificationOTPFragment, bundle)
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