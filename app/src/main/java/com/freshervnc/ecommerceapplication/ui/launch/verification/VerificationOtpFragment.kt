package com.freshervnc.ecommerceapplication.ui.launch.verification

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.VerifyOtpRequest
import com.freshervnc.ecommerceapplication.databinding.FragmentVerificationOTPBinding
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils


class VerificationOtpFragment : BaseFragment() {
    private lateinit var binding : FragmentVerificationOTPBinding
    override var isVisibleActionBar: Boolean = false
    private val viewModel by activityViewModels<VerificationOtpViewModel>()
    private lateinit var prerences : PreferencesUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentVerificationOTPBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        prerences = PreferencesUtils(requireContext())
        viewModel.email = arguments?.getString("email") ?: ""

    }

    @SuppressLint("StringFormatInvalid")
    override fun setView() {
        binding.noteOtp.text = requireContext().getString(R.string.note_otp ,viewModel.email)
    }

    override fun setAction() {
        binding.btnVerifyOtp.setOnClickListener {
            viewModel.verifyOtp(
                VerifyOtpRequest(
                    code = ""
                )
            )
        }
    }

    override fun setObserve() {
    }

}