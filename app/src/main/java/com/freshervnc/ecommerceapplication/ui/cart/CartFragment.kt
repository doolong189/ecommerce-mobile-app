package com.freshervnc.ecommerceapplication.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.adapter.CartAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.CreateOrderRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateOrderResponse
import com.freshervnc.ecommerceapplication.data.enity.DeleteCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartRequest
import com.freshervnc.ecommerceapplication.data.enity.GetCartResponse
import com.freshervnc.ecommerceapplication.data.enity.UpdateCartRequest
import com.freshervnc.ecommerceapplication.data.enity.UpdateCartResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentCartBinding
import com.freshervnc.ecommerceapplication.data.model.ProductOfCart
import com.freshervnc.ecommerceapplication.ui.order.OrderViewModel
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.SocketIOManager
import com.freshervnc.ecommerceapplication.utils.Utils


class CartFragment : BaseFragment() {
    private lateinit var binding : FragmentCartBinding
    override var isVisibleActionBar: Boolean = true
    private val viewModel by activityViewModels<CartViewModel>()
    private val orderViewModel by activityViewModels<OrderViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var cartAdapter = CartAdapter()
    private var socketIO = SocketIOManager()
    companion object{
        var products: MutableList<ProductOfCart>? = mutableListOf()
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
        socketIO.connect()
        binding.cartBtnCheckOut.setOnClickListener {
//            orderViewModel.createOrder(CreateOrderRequest(idClient = preferences.userId, idShipper = "", products = products))
            socketIO.sendMessage("abc")
        }
        cartAdapter.onClickItemSubQuantity { item, position ->
            if (item.quantity!! >= 0 ){
                viewModel.updateCart(UpdateCartRequest(idUser = preferences.userId , idProduct = item.id , quantity = -1))
                cartAdapter.updateTotalPrice()
            }
        }
        cartAdapter.onClickItemAddQuantity { item, position ->
            viewModel.updateCart(UpdateCartRequest(idUser = preferences.userId , idProduct = item.id , quantity = 1))
            cartAdapter.updateTotalPrice()
        }

        cartAdapter.onClickItemDelete { item, position ->
            viewModel.deleteCart(DeleteCartRequest(idUser = preferences.userId , idProduct = item.id))
        }
    }

    override fun setObserve() {
        viewModel.getCartResult().observe(viewLifecycleOwner, Observer {
            getCartResult(it)
        })
        orderViewModel.createOrderResult().observe(viewLifecycleOwner, Observer {
            createOrderResult(it)
        })
        viewModel.updateCartResult().observe(viewLifecycleOwner, Observer {
            updateCartResult(it)
        })
    }

    private fun getCartResult(event : Event<Resource<GetCartResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when(response){
                is Resource.Success -> {
                    binding.cartPgBar.visibility = View.GONE
                    response.data?.let {
                        if (it.response.products?.isEmpty() == true) {
                            binding.lnCartEmpty.visibility = View.VISIBLE
                        }else{
                            binding.lnCartEmpty.visibility = View.GONE
                            cartAdapter.submitList(it.response.products!!)
                            binding.cartTvTotalNumberProduct.text = "${it.response.totalNumber}"
                            binding.cartTvTotalNumber.text = ""
                            binding.cartTvDiscount.text = "${it.response.discount} %"
                            binding.cartTvTotalPrice.text = "${Utils.formatPrice(it.response.totalPrice!!)} đ"
                            products!!.clear()
                            products!!.addAll(it.response.products)
                        }
                    }
                }
                is Resource.Loading -> {
                    binding.cartPgBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.cartPgBar.visibility = View.GONE
                }
            }
        }
    }

    private fun createOrderResult(event : Event<Resource<CreateOrderResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when(response){
                is Resource.Success -> {
                    binding.cartPgBar.visibility = View.GONE
                    response.data?.let {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    binding.cartPgBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.cartPgBar.visibility = View.GONE
                    Toast.makeText(requireContext(),response.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateCartResult(event : Event<Resource<UpdateCartResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when ( response ){
                is Resource.Error -> {
                    binding.cartPgBar.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.cartPgBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.cartPgBar.visibility = View.GONE
                    response.data?.let {
                        binding.cartTvTotalNumberProduct.text = "${it.response.totalNumber}"
                        binding.cartTvTotalNumber.text = ""
                        binding.cartTvDiscount.text = "${it.response.discount} %"
                        binding.cartTvTotalPrice.text = "${Utils.formatPrice(it.response.totalPrice!!)} đ"
                    }
                }
            }
        }
    }

}