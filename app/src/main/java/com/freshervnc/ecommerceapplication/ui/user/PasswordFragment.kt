package com.freshervnc.ecommerceapplication.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.databinding.FragmentPasswordBinding


class PasswordFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPasswordBinding.inflate(layoutInflater, container, false)
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