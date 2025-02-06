package com.freshervnc.ecommerceapplication.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentUserBinding
import com.freshervnc.ecommerceapplication.ui.cart.CartActivity
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource


class UserFragment : BaseFragment() {

    override var isVisibleActionBar: Boolean = true
    private lateinit var binding : FragmentUserBinding
    private val viewModel by activityViewModels<UserViewModel>()
    private lateinit var preferences : PreferencesUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())
    }

    override fun setView() {
        viewModel.getUserInfo(GetUserInfoRequest(preferences.userId!!))
    }

    override fun setAction() {
        val navi = requireActivity().supportFragmentManager.fragments[0] as NavHostFragment
        binding.userBtnEditProfile.setOnClickListener { }

        binding.userBtnChangePassword.setOnClickListener {
            navi.navController.navigate(R.id.passwordFragment)
        }

        binding.userBtnStore.setOnClickListener {

        }

        binding.userBtnBillOder.setOnClickListener {
            navi.navController.navigate(R.id.orderFragment)
        }

        binding.userBtnHelp.setOnClickListener { }

        binding.userBtnSetting.setOnClickListener {  }
        binding.userBtnLogOut.setOnClickListener {  }
    }


    override fun setObserve() {
        viewModel.getUserInfoResult().observe(viewLifecycleOwner, Observer {
            getUserInfosResult(it)
        })
    }

    private fun getUserInfosResult(event: Event<Resource<GetUserInfoResponse>>) {
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    response.data.let {
                        binding.userTvName.text = it?.user?.name ?: ""
                        binding.userTvEmail.text = it?.user?.email ?: ""
                        Glide.with(requireContext()).load(it?.user?.image).into(binding.userImgUser)
                    }
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.GONE


                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.VISIBLE

                }
            }
        }
    }
}