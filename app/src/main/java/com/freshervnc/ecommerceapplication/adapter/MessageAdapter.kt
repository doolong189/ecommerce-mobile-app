package com.freshervnc.ecommerceapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.adapter.CategoryAdapter.ProductViewHolder
import com.freshervnc.ecommerceapplication.databinding.ItemCategoryBinding
import com.freshervnc.ecommerceapplication.databinding.ItemHistoryMessageBinding
import com.freshervnc.ecommerceapplication.model.Category
import com.freshervnc.ecommerceapplication.model.UserInfo
import java.text.SimpleDateFormat
import java.util.Date
private var onClickItem: ((id: UserInfo, position: Int) -> Unit)? = null

class MessageAdapter() : RecyclerView.Adapter<MessageAdapter.UserViewHolder>() {
    private var senderId : String = "";
    private var senderRoom : String = "";

    private var list: List<UserInfo> = listOf()
    fun onClickItemMessage(id: ((id: UserInfo, position: Int) -> Unit)) {
        onClickItem = id
    }

    class UserViewHolder(private val binding : ItemHistoryMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: UserInfo) {
            binding.run {
                itemHistoryMessageTvName.text = item.name
                Glide.with(itemView.context).load(item.image)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(itemHistoryMessageImage)
                itemView.setOnClickListener {
                    onClickItem?.let {
                        it(item, adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            ItemHistoryMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount() = list.size

//    fun setMessage(messages : List<MessageModel>) {
//        this.messages = messages
//        notifyDataSetChanged()
//    }
}