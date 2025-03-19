package com.freshervnc.ecommerceapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemReceiverBinding
import com.freshervnc.ecommerceapplication.databinding.ItemSenderBinding
import com.freshervnc.ecommerceapplication.model.Chat
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Utils


class ChatAdapter(private val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2
    private var preferences: PreferencesUtils = PreferencesUtils(context)
    private var messageList: List<Chat> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sender, parent, false)
            SentViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receiver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (preferences.userId == message.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SentViewHolder) {
            holder.binding.message.text = message.messageText
            holder.binding.timestamp.text = Utils.convertLongToTimeStamp(message.timestamp)
            if (message.messageText == "Photo" || message.messageText == "Camera") {
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.message.visibility = View.GONE
                Glide.with(holder.itemView.context).load(message.messageImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.image)
                holder.binding.image.setBackgroundResource(R.drawable.sender_photo_bg)
            }else if (message.messageText == "Mặt hàng này còn chứ?"){
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.message.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(message.messageImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.image)
            }
        } else if (holder is ReceiverViewHolder) {
            holder.binding.message.text = message.messageText
            holder.binding.timestamp.text = Utils.convertLongToTimeStamp(message.timestamp)
            if (message.messageText == "Photo" || message.messageText == "Camera") {
                holder.binding.lnImage.visibility = View.VISIBLE
                holder.binding.message.visibility = View.GONE
                Glide.with(holder.itemView.context).load(message.messageImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.image)
            } else if (message.messageText == "Mặt hàng này còn chứ?"){
                holder.binding.lnImage.visibility = View.VISIBLE
                holder.binding.message.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(message.messageImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.image)
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemSenderBinding = ItemSenderBinding.bind(itemView)
    }

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemReceiverBinding = ItemReceiverBinding.bind(itemView)
    }

    fun submitList(message: List<Chat>) {
        this.messageList = message
        notifyDataSetChanged()
    }
}