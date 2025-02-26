package com.freshervnc.ecommerceapplication.ui.main.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductResponse
import com.freshervnc.ecommerceapplication.databinding.FragmentDetailProductBinding
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils


class DetailProductFragment : BaseFragment() {
    private lateinit var binding : FragmentDetailProductBinding
    override var isVisibleActionBar: Boolean = false
    private val viewModel by activityViewModels<DetailProductViewModel>()
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
        val productId = arguments?.getString("productId") ?: ""
        viewModel.getGetDetailProduct(GetDetailProductRequest(id = productId))
    }

    override fun setView() {
    }

    override fun setAction() {
        binding.detailBtnSend.setOnClickListener {

        }
    }

    override fun setObserve() {
        viewModel.getGetDetailProductResult().observe(viewLifecycleOwner , Observer {
            getGetDetailProductResult(it)
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
}