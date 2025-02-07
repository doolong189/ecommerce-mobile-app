package com.freshervnc.ecommerceapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.data.enity.Product
import com.freshervnc.ecommerceapplication.databinding.ItemCartBinding
import com.freshervnc.ecommerceapplication.databinding.ItemCategoryBinding
import com.freshervnc.ecommerceapplication.model.Cart
import com.freshervnc.ecommerceapplication.model.Category
import com.freshervnc.ecommerceapplication.utils.Utils

class CartAdapter() : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private var list: List<Product> = listOf()
    class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Product) {
            binding.run {
                Glide.with(binding.root.context)
                    .load(item.image)
                    .placeholder(R.drawable.logo_app)
                    .into(itemCartImgView)
                itemCartTvName.text = item.name
                itemCartTvPrice.text = "${Utils.formatPrice(item.price!!)} đ"
                itemCartTvQuantity.text = "Số lượng ${item.quantity}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun submitList(carts: List<Product>) {
        list = carts
        notifyDataSetChanged()
    }
}