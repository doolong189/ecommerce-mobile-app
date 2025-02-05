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
import com.freshervnc.ecommerceapplication.model.Message
import com.freshervnc.ecommerceapplication.model.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
private var onClickItem: ((id: Message, position: Int) -> Unit)? = null

class MessageAdapter() : RecyclerView.Adapter<MessageAdapter.UserViewHolder>() {
    private var senderId : String = ""
    private var senderRoom : String = ""

    private var list: List<Message> = listOf()
    fun onClickItemMessage(id: ((id: Message, position: Int) -> Unit)) {
        onClickItem = id
    }

    class UserViewHolder(private val binding : ItemHistoryMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Message) {
            binding.run {
                itemHistoryMessageTvName.text = item.senderName
                Glide.with(itemView.context).load(item.senderAvatar)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(itemHistoryMessageImage)
                var senderRoom : String = ""
                senderRoom = item.senderId + item.receiverId
                FirebaseDatabase.getInstance().reference
                    .child("Chats")
                    .child(item.senderId)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val lastMsg = snapshot.child("lastMsg").getValue(String::class.java)
                                val time = snapshot.child("lastMsgTime").getValue(Long::class.java)!!
                                val dateFormat = SimpleDateFormat("hh:mm a")
                                itemHistoryMessageTvTime.text = dateFormat.format(Date(time))
                                itemHistoryMessageTvMessage.text = lastMsg
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
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

    fun setMessage(messages : List<Message>) {
        list = messages
        notifyDataSetChanged()
    }
}