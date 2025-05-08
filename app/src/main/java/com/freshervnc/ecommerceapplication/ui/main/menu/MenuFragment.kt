package com.freshervnc.ecommerceapplication.ui.main.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.CategoryAdapter
import com.freshervnc.ecommerceapplication.adapter.ProductAdapter
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetCategoryResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductWithCategoryResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentMenuBinding
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingViewModel
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource


class MenuFragment : BaseFragment() {
    private lateinit var binding: FragmentMenuBinding
    private val viewModel by activityViewModels<ShoppingViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var categoryAdapter = CategoryAdapter()
    private var productAdapter = ProductAdapter()
    override var isVisibleActionBar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())

        binding.rcCategory.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL , false)
        binding.rcCategory.run { adapter = CategoryAdapter().also { categoryAdapter = it } }

        binding.rcProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcProduct.run { adapter = ProductAdapter().also { productAdapter = it } }

        viewModel.getCategory()
        viewModel.getProduct(GetProductRequest(id = preferences.userId))
    }

    override fun setView() {
    }

    override fun setAction() {
        categoryAdapter.onClickItemCategory { item, position ->
            viewModel.getProductWithCategory(GetProductWithCategoryRequest(idCategory = item._id , idUser = preferences.userId))
        }

        productAdapter.onClickItemProduct { id, position ->
            val bundle = Bundle().apply { putString("productId", id._id) }
            findNavController().navigate(R.id.action_shoppingFragment_to_detailProductFragment , bundle)
        }
    }

    override fun setObserve() {
        viewModel.getCategoryResult().observe(viewLifecycleOwner, Observer {
            getCategoryResult(it)
        })
        viewModel.getProductResult().observe(viewLifecycleOwner, Observer {
            getProductResult(it)
        })
        viewModel.getProductWithCategoryResult().observe(viewLifecycleOwner , Observer{
            getProductWithCategoryResult(it)
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

    private fun getProductWithCategoryResult(event : Event<Resource<GetProductWithCategoryResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when(response){
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
                    response.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}