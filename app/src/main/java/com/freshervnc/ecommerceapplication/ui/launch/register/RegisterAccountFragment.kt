package com.freshervnc.ecommerceapplication.ui.launch.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.databinding.FragmentRegisterAccountBinding


class RegisterAccountFragment : BaseFragment() {
    override var isVisibleActionBar = false
    private lateinit var binding : FragmentRegisterAccountBinding
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