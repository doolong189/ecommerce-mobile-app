package com.freshervnc.ecommerceapplication.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment


class PasswordFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password, container, false)
    }
    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun setView() {
        TODO("Not yet implemented")
    }

    override fun setAction() {
        TODO("Not yet implemented")
    }

    override fun setObserve() {
        TODO("Not yet implemented")
    }
}