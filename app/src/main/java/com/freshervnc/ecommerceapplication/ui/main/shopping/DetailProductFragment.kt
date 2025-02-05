package com.freshervnc.ecommerceapplication.ui.main.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment


class DetailProductFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_product, container, false)
    }

    override fun initView() {
    }

    override fun setView() {
    }

    override fun setAction() {
    }

    override fun setObserve() {
    }
}