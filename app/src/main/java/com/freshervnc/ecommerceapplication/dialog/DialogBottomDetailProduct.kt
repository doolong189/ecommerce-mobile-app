package com.freshervnc.ecommerceapplication.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.freshervnc.ecommerceapplication.adapter.ProductAdapter
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductRequest
import com.freshervnc.ecommerceapplication.data.enity.GetDetailProductResponse
import com.freshervnc.ecommerceapplication.data.enity.GetProductSimilarRequest
import com.freshervnc.ecommerceapplication.data.enity.GetProductSimilarResponse
import com.freshervnc.ecommerceapplication.databinding.BottomDialogInfoProductBinding
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingViewModel
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogBottomDetailProduct : BottomSheetDialogFragment() {
    private lateinit var binding : BottomDialogInfoProductBinding
    private var productAdapter = ProductAdapter()
    private val shoppingViewModel by activityViewModels<ShoppingViewModel>()
    private lateinit var preferences : PreferencesUtils
    private var googleMap : GoogleMap? = null
    companion object {
        private const val ARG_ID_PRODUCT = "id_product"
        fun newInstance(idProduct: String): DialogBottomDetailProduct {
            val fragment = DialogBottomDetailProduct()
            val args = Bundle()
            args.putString(ARG_ID_PRODUCT, idProduct)
            fragment.arguments = args
            return fragment
        }
    }
    private var idProduct: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idProduct = arguments?.getString(ARG_ID_PRODUCT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomDialogInfoProductBinding.inflate(layoutInflater, container, false)
        preferences = PreferencesUtils(inflater.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setAction()
        setObserve()
    }
    private fun initView(){
        idProduct?.let {
            binding.rcSimilarProduct.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.rcSimilarProduct.run { adapter = ProductAdapter().also { productAdapter = it } }
            shoppingViewModel.getDetailProduct(GetDetailProductRequest(id = it))
        }
    }

    private fun setAction(){
        binding.btDirection.setOnClickListener {
            direction()
        }
        binding.btCall.setOnClickListener {  }
        binding.btSendMessage.setOnClickListener {  }
    }

    private fun setObserve(){
        shoppingViewModel.getProductSimilarResult().observe(this, Observer {
            getProductSimilarResult(it)
        })

        shoppingViewModel.getDetailProductResult().observe(this, Observer {
            getDetailProductResult(it)
        })
    }

    private fun getDetailProductResult(event: Event<Resource<GetDetailProductResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when(response){
                is Resource.Error -> {
                    binding.pbBar.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.pbBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.pbBar.visibility = View.GONE
                    response.data?.data?.let{
                        binding.tvProduct.text = "Sản phẩm: ${it.name}"
                        binding.tvStore.text = "Của hàng: ${it.idUser?.name}"

                        shoppingViewModel.getProductSimilar(GetProductSimilarRequest(idUser = preferences.userId.toString() , idProduct = it._id))
                    }
                }
            }
        }
    }

    private fun getProductSimilarResult(event : Event<Resource<GetProductSimilarResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Error -> {
                    binding.pbBar.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.pbBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.pbBar.visibility = View.GONE
                    response.data?.let { resultResponse ->
                        productAdapter.submitList(resultResponse.products!!)
                    }
                }
            }
        }
    }

    private fun direction(){
        val latLngOrigin = LatLng(preferences.getUserLoc()!![1], preferences.getUserLoc()!![0])
        val latLngDestination = LatLng(10.311795,123.915864)
        this.googleMap!!.addMarker(MarkerOptions().position(latLngOrigin).title("Ayala"))
        this.googleMap!!.addMarker(MarkerOptions().position(latLngDestination).title("SM City"))
        this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 14.5f))
    }
}
