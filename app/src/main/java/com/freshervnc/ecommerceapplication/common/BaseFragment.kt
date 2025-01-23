package com.freshervnc.ecommerceapplication.common

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    abstract fun initView()
    abstract fun setView()
    abstract fun setAction()
    abstract fun setObserve()
    abstract var isVisibleActionBar: Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(isVisibleActionBar)
//        view.fitsSystemWindows = true
        initView()
        setView()
        setAction()
        setObserve()
    }
    private fun setActionBar(isVisible: Boolean) {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            if (isVisible) {
                show()
                setDisplayHomeAsUpEnabled(true)
            } else {
                hide()
                setDisplayHomeAsUpEnabled(false)
            }
        }
    }
}