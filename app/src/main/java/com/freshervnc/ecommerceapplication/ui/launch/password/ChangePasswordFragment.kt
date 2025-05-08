package com.freshervnc.ecommerceapplication.ui.launch.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.databinding.FragmentChangePasswordBinding


class ChangePasswordFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun initView() {
    }

    override fun setView() {
    }

    override fun setAction() {
        binding.changeBtnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setObserve() {
    }
}