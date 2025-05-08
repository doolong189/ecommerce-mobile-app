package com.freshervnc.ecommerceapplication.ui.launch.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.databinding.FragmentRegisterAccountBinding


class RegisterAccountFragment : BaseFragment() {
    override var isVisibleActionBar = false
    private lateinit var binding : FragmentRegisterAccountBinding
    private val viewModel by activityViewModels<RegisterAccountViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
    }

    override fun setView() {
    }

    override fun setAction() {
        binding.tvHaveAccount.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setObserve() {
    }

}