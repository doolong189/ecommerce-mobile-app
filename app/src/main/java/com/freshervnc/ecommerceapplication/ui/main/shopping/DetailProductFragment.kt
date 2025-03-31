package com.freshervnc.ecommerceapplication.ui.main.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.ImageAdapter
import com.freshervnc.ecommerceapplication.adapter.ReviewAdapter
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.CreateCartRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateCartResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetReviewWithProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetReviewWithProductResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentDetailProductBinding
import com.freshervnc.ecommerceapplication.ui.cart.CartViewModel
import com.freshervnc.ecommerceapplication.ui.main.MainActivity
import com.freshervnc.ecommerceapplication.utils.Constants
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils


class DetailProductFragment : BaseFragment() {
    private lateinit var binding : FragmentDetailProductBinding
    override var isVisibleActionBar: Boolean = false
    private val viewModel by activityViewModels<DetailProductViewModel>()
    private val cartViewModel by activityViewModels<CartViewModel>()
    private var quantity = 0
    private lateinit var preferences : PreferencesUtils
    private var reviewAdapter = ReviewAdapter()
    private var imageAdapter = ImageAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())
        val productId = arguments?.getString("productId") ?: ""

        binding.rcReview.layoutManager = LinearLayoutManager(requireContext())
        binding.rcReview.run { adapter = ReviewAdapter().also { reviewAdapter = it } }

        binding.rcImageProduct.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL , false)
        binding.rcImageProduct.run { adapter = ImageAdapter().also { imageAdapter = it } }

        viewModel.getGetDetailProduct(GetDetailProductRequest(id = productId))
        viewModel.getReviewWithProduct(GetReviewWithProductRequest(id = productId))
    }

    override fun setView() {
    }

    override fun setAction() {
        val productId = arguments?.getString("productId") ?: ""

        binding.imgSub.setOnClickListener {
            if (quantity <= 0){
                quantity = 0
            }else {
                quantity--
            }
            binding.tvQuantity.text = "$quantity"
        }

        binding.imgAdd.setOnClickListener {
            quantity++
            binding.tvQuantity.text = "$quantity"
        }

        binding.btAddToCart.setOnClickListener{
            if (quantity == 0) {
                quantity += 1
            }
            cartViewModel.createCart(CreateCartRequest(idProduct = productId, idUser = preferences.userId , quantity = quantity))
        }

        binding.detailImageBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setObserve() {
        viewModel.getGetDetailProductResult().observe(viewLifecycleOwner , Observer {
            getGetDetailProductResult(it)
        })
        cartViewModel.createCartResult().observe(viewLifecycleOwner, Observer {
            createCart(it)
        })
        viewModel.getReviewWithProductResult().observe(viewLifecycleOwner , Observer {
            getReviewWithProductResult(it)
        })
    }

    private fun getGetDetailProductResult(event: Event<Resource<GetDetailProductResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when ( response ){
                is Resource.Error -> {
                    binding.pgBar.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.pgBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data?.data?.let {
                        Glide.with(requireContext())
                            .load(it.image[0])
                            .placeholder(R.drawable.image_def)
                            .into(binding.detailImageProduct)
                        binding.detailTvNameProduct.text = it.name
                        binding.detailTvPrice.text = "Giá: "+Utils.formatPrice(it.price) + " đ"
                        binding.detailTvQuantity.text = "Số lượng: "+it.quantity
                        binding.detailTvDescription.text = "Mô tả: "+it.description

                        //store
                        Glide.with(requireContext())
                            .load(it.idUser?.image)
                            .into(binding.detailImageUser)
                        binding.detailTvNameUser.text = it.idUser?.name
                        binding.detailTvAddress.text = it.idUser?.address

                        //
                        imageAdapter.submitList(it.image)
                        imageAdapter.onClickItemImage { item, position ->
                            Glide.with(requireContext())
                                .load(item)
                                .placeholder(R.drawable.image_def)
                                .into(binding.detailImageProduct)
                        }
                    }
                }
            }
        }
    }

    private fun getReviewWithProductResult(event : Event<Resource<GetReviewWithProductResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when ( response ){
                is Resource.Error -> {
                    binding.pgBar.visibility = View.GONE
                    Log.e(Constants.TAG,response.message.toString())
                }
                is Resource.Loading -> {
                    binding.pgBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data?.let {
                        reviewAdapter.submitList(it.review!!)
                        binding.tvAmountReview.text = "(${it.review!!.size})"
                    }
                }
            }
        }
    }

    private fun createCart(event : Event<Resource<CreateCartResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when ( response ){
                is Resource.Error -> {
                    binding.pgBar.visibility = View.GONE
                    Toast.makeText(requireContext(),response.message,Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.pgBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data?.let {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).GONE()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).Visiable()
    }
}