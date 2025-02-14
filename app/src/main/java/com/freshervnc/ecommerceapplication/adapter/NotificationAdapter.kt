package com.freshervnc.ecommerceapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemCategoryBinding
import com.freshervnc.ecommerceapplication.databinding.ItemNotificationBinding
import com.freshervnc.ecommerceapplication.model.Category
import com.freshervnc.ecommerceapplication.model.Notification
private var onClickItem: ((id: Notification, position: Int) -> Unit)? = null

class NotificationAdapter() : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private var list: List<Notification> = listOf()
    fun onClickItemNotification(id: ((id: Notification, position: Int) -> Unit)) {
        onClickItem = id
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Notification) {
            binding.run {
                binding.itemNotificationTvTitle.text = item.title
                binding.itemNotificationTvBody.text = item.body
                itemView.setOnClickListener {
                    onClickItem?.let {
                        it(item, adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun submitList(Notifications: List<Notification>) {
        list = Notifications
        notifyDataSetChanged()
    }
}