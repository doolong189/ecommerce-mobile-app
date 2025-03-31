package com.freshervnc.ecommerceapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemCategoryBinding
import com.freshervnc.ecommerceapplication.databinding.ItemImageBinding
import com.freshervnc.ecommerceapplication.model.Category
import com.freshervnc.ecommerceapplication.model.ImageModel

private var onClickItem: ((id: String, position: Int) -> Unit)? = null
class ImageAdapter() : RecyclerView.Adapter<ImageAdapter.ProductViewHolder>() {
    private var list: List<String> = listOf()

    fun onClickItemImage(id: ((item : String, position: Int) -> Unit)) {
        onClickItem = id
    }
    class ProductViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: String) {
            binding.run {
                Glide.with(binding.root.context)
                    .load(item)
                    .placeholder(R.drawable.image_def)
                    .into(binding.imageView)
                itemView.setOnClickListener {
                    onClickItem?.let {
                        it(item, adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun submitList(images: List<String>) {
        list = images
        notifyDataSetChanged()
    }
}