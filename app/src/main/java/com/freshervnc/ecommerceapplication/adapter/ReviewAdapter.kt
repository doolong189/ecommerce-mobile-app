package com.freshervnc.ecommerceapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemCategoryBinding
import com.freshervnc.ecommerceapplication.databinding.ItemReviewsBinding
import com.freshervnc.ecommerceapplication.model.Category
import com.freshervnc.ecommerceapplication.model.Review

class ReviewAdapter() : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    private var list: List<Review> = listOf()

    class ReviewViewHolder(
        private val binding: ItemReviewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Review) {
            binding.run {
                Glide.with(binding.root.context)
                    .load(item.idUser?.image)
                    .placeholder(R.drawable.logo_app)
                    .into(binding.image)
                binding.username.text = item.idUser?.name
                binding.date.text = item.date
                binding.tvContent.text = item.title
                binding.ratingBar.rating = item.rating
                binding.tvRatingBar.text = "( ${item.rating} )"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding =
            ItemReviewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun submitList(review: List<Review>) {
        list = review
        notifyDataSetChanged()
    }
}