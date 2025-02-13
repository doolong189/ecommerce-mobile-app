package com.freshervnc.ecommerceapplication.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.MutableIntList
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.adapter.CartAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.AddOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartResponse
import com.freshervnc.ecommerceapplication.data.enity.Product
import com.freshervnc.ecommerceapplication.databinding.FragmentCartBinding
import com.freshervnc.ecommerceapplication.ui.order.OrderViewModel
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import java.util.ArrayList
import java.util.Date


class CartFragment : BaseFragment() {
    private lateinit var binding : FragmentCartBinding
    override var isVisibleActionBar: Boolean = false
    private val viewModel by activityViewModels<CartViewModel>()
    private val orderViewModel by activityViewModels<OrderViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var cartAdapter = CartAdapter()
    companion object{
        var idClient = ""
        var products: MutableList<Product>? = mutableListOf()
    }
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
        val date = Date()
        products?.map { item ->
            binding.cartBtnCheckOut.setOnClickListener {
                orderViewModel.addOrder(
                    AddOrderRequest(
                        totalPrice = (item.price!!.toInt() * item.quantity!!.toInt()),
                        date = date.time.toString(),
                        receiptStatus = 0,
                        idClient = preferences.userId,
                        idStore = item.idStore,
                        idShipper = null,
                        products = products
                    )
                )
            }
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
                        binding.cartTvTotalNumberProduct.text = "${it.response.totalNumber}"
                        binding.cartTvTotalNumber.text = ""
                        binding.cartTvDiscount.text = "${it.response.discount} %"
                        binding.cartTvTotalPrice.text = "${Utils.formatPrice(it.response.totalPrice!!)} đ"

                        products!!.addAll(it.response.products)
                    }
                }
                is Resource.Loading -> {
                    binding.cartPbBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.cartPbBar.visibility = View.GONE
                    binding.tvEmpty.text = response.message
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }
        }
    }
}