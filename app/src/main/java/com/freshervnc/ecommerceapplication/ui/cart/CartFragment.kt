package com.freshervnc.ecommerceapplication.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.adapter.CartAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.AddCartResponse
import com.freshervnc.ecommerceapplication.data.enity.GetCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentCartBinding
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils


class CartFragment : BaseFragment() {
    private lateinit var binding : FragmentCartBinding
    override var isVisibleActionBar: Boolean = false
    private val viewModel by activityViewModels<CartViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var cartAdapter = CartAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())
    }

    override fun setView() {
        binding.cartRcView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRcView.run { adapter = CartAdapter().also { cartAdapter = it } }

        viewModel.getCart(GetCartRequest(idUser = preferences.userId))
    }

    override fun setAction() {
        binding.cartBtnCheckOut.setOnClickListener {

        }
    }

    override fun setObserve() {
        viewModel.getCartResult().observe(viewLifecycleOwner, Observer {
            getCartResult(it)
        })
    }

    private fun getCartResult(event : Event<Resource<GetCartResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when(response){
                is Resource.Success -> {
                    binding.cartPbBar.visibility = View.GONE
                    response.data?.let {
                        cartAdapter.submitList(it.response.products!!)

                        binding.cartTvTotalNumber.text = "${it.response.totalNumber}"
                        binding.cartTvDiscount.text = "${it.response.discount}"
                        binding.cartTvTotalPrice.text = "${Utils.formatPrice(it.response.totalPrice!!)}"
                    }
                }
                is Resource.Loading -> {
                    binding.cartPbBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.cartPbBar.visibility = View.GONE
                }
            }
        }
    }
}