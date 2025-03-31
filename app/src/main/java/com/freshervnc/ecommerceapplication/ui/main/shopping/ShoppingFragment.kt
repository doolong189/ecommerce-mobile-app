package com.freshervnc.ecommerceapplication.ui.main.shopping

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.CategoryAdapter
import com.freshervnc.ecommerceapplication.adapter.ProductAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentShoppingBinding
import com.freshervnc.ecommerceapplication.ui.cart.CartActivity
import com.freshervnc.ecommerceapplication.ui.notification.NotificationActivity
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource


class ShoppingFragment : BaseFragment() {
    override var isVisibleActionBar: Boolean = false
    private lateinit var binding : FragmentShoppingBinding
    private val viewModel by activityViewModels<ShoppingViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var categoryAdapter = CategoryAdapter()
    private var productAdapter = ProductAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentShoppingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())

        binding.rcCategory.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL , false)
        binding.rcCategory.run { adapter = CategoryAdapter().also { categoryAdapter = it } }

        binding.rcProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcProduct.run { adapter = ProductAdapter().also { productAdapter = it } }

        viewModel.getCategory()
        viewModel.getProduct(GetProductRequest(id = preferences.userId))
    }

    override fun setView() {
        Glide.with(binding.root.context)
            .load(preferences.image)
            .placeholder(R.drawable.logo_app)
            .into(binding.image)
        binding.username.text = preferences.userName
    }

    override fun setAction() {
        categoryAdapter.onClickItemCategory { id, position ->
        }

        productAdapter.onClickItemProduct { id, position ->
            val bundle = Bundle().apply { putString("productId", id._id) }
            findNavController().navigate(R.id.action_shoppingFragment_to_detailProductFragment , bundle)
        }

        binding.icCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        binding.icNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
    }

    override fun setObserve() {
        viewModel.getCategoryResult().observe(viewLifecycleOwner, Observer {
            getCategoryResult(it)
        })
        viewModel.getProductResult().observe(viewLifecycleOwner, Observer {
            getProductResult(it)
        })
    }

    private fun getCategoryResult(event: Event<Resource<GetCategoryResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    response.data?.let {
                        categoryAdapter.submitList(it.categorys!!)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE

                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getProductResult(event: Event<Resource<GetProductResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    response.data?.let {
                        productAdapter.submitList(it.products!!)
                    }
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

}