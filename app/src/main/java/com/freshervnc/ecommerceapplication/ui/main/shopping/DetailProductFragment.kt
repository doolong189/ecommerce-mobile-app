package com.freshervnc.ecommerceapplication.ui.main.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.AddCartRequest
import com.freshervnc.ecommerceapplication.data.enity.AddCartResponse
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentDetailProductBinding
import com.freshervnc.ecommerceapplication.ui.cart.CartViewModel
import com.freshervnc.ecommerceapplication.ui.main.MainActivity
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
        viewModel.getGetDetailProduct(GetDetailProductRequest(id = productId))
    }

    override fun setView() {
    }

    override fun setAction() {
        val productId = arguments?.getString("productId") ?: ""
        binding.detailBtnSend.setOnClickListener {

        }

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
            cartViewModel.addCart(AddCartRequest(idProduct = productId, idUser = preferences.userId , quantity = quantity))
        }
    }

    override fun setObserve() {
        viewModel.getGetDetailProductResult().observe(viewLifecycleOwner , Observer {
            getGetDetailProductResult(it)
        })
        cartViewModel.addCartResult().observe(viewLifecycleOwner, Observer {
            addCart(it)
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
                            .load(it.image)
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
                    }
                }
            }
        }
    }

    private fun addCart(event : Event<Resource<AddCartResponse>>){
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