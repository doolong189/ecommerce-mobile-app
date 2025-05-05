package com.freshervnc.ecommerceapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemCategoryBinding
import com.freshervnc.ecommerceapplication.data.model.Category


private var onClickItem: ((id: Category, position: Int) -> Unit)? = null

class CategoryAdapter() : RecyclerView.Adapter<CategoryAdapter.ProductViewHolder>() {
    private var list: List<Category> = listOf()

    fun onClickItemCategory(id: ((item : Category, position: Int) -> Unit)) {
        onClickItem = id
    }

    class ProductViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Category) {
            binding.run {
                Glide.with(binding.root.context)
                    .load(item.image)
                    .placeholder(R.drawable.logo_app)
                    .into(binding.itemCategoryImageView)
                binding.itemCategoryTvName.text = item.name
                itemView.setOnClickListener {
                    onClickItem?.let {
                        it(item, adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun submitList(products: List<Category>) {
        list = products
        notifyDataSetChanged()
    }
}
