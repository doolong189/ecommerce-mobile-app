package com.freshervnc.ecommerceapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemDetailOrderBinding
import com.freshervnc.ecommerceapplication.model.Product
import com.freshervnc.ecommerceapplication.model.Products
import com.freshervnc.ecommerceapplication.utils.Utils

class DetailOrderAdapter() : RecyclerView.Adapter<DetailOrderAdapter.DetailOrderViewHolder>() {
    private var list: List<Products> = listOf()
    class DetailOrderViewHolder(
        private val binding: ItemDetailOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Products) {
            binding.run {
                Glide.with(binding.root.context)
                    .load(item.product.image)
                    .placeholder(R.drawable.logo_app)
                    .into(itemDetailImgView)
                itemDetailTvName.text = item.product.name
                itemDetailTvPrice.text = "${Utils.formatPrice(item.product.price!!)} đ"
                itemDetailTvQuantity.text = "Số lượng ${item.quantity}"
                val intoMoney = item.product.price * item.quantity
                itemDetailTvIntoMoney.text = "Thành tiền: ${Utils.formatPrice(intoMoney)} đ"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailOrderViewHolder {
        val binding =
            ItemDetailOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: DetailOrderViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun submitList(products: List<Products>) {
        list = products
        notifyDataSetChanged()
    }
}