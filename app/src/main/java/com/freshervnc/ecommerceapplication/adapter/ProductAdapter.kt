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

class ProductAdapter() : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var list: List<Product> = listOf()
    class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Product) {
            binding.run {
                val itemCategory : Category = item.idCategory!!
                itemProductTvName.text = item.name
                itemProductTvPrice.text = Utils.formatPrice(item.price)  + " đ"
                itemProductTvCategory.visibility = View.VISIBLE
                itemProductTvCategory.text = itemCategory.name
                Glide.with(itemView).load(item.image)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(itemProductImageView)
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