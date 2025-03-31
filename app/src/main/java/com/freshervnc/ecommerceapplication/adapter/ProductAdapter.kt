package com.freshervnc.ecommerceapplication.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemCategoryBinding
import com.freshervnc.ecommerceapplication.databinding.ItemProductBinding
import com.freshervnc.ecommerceapplication.model.Category
import com.freshervnc.ecommerceapplication.model.Product
import com.freshervnc.ecommerceapplication.utils.Utils

private var onClickItem: ((id: Product, position: Int) -> Unit)? = null

class ProductAdapter() : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var list: List<Product> = listOf()

    fun onClickItemProduct(id: ((item : Product, position: Int) -> Unit)) {
        onClickItem = id
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(item: Product) {
            binding.run {
                val itemCategory : Category = item.idCategory!!
                tvName.text = item.name
                tvPrice.text = Utils.formatPrice(item.price)  + "đ"
                tvCategory.text = itemCategory.name
                Glide.with(itemView)
                    .load(item.image[0])
                    .placeholder(R.drawable.image_def)
                    .into(imageView)
                itemView.setOnClickListener {
                    onClickItem?.let {
                        it(item, adapterPosition)
                    }
                }
                if (item.discount > 0) {
                    tvDiscount.visibility = View.VISIBLE
                    tvDiscount.text = "${item.discount} %"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(products: List<Product>) {
        list = products
        notifyDataSetChanged()
    }
}