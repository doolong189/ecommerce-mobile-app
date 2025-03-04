package com.freshervnc.ecommerceapplication.adapter

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SentViewHolder) {
            holder.binding.itemSentTextview.text = message.messageText
            if (message.messageText == "Photo" || message.messageText
                == "camera") {
                holder.binding.itemSendPhotoImage.visibility = View.VISIBLE
                holder.binding.itemSentTextview.visibility = View.GONE
                holder.binding.linearLayout.setBackgroundResource(R.drawable.border_shadow)
                Glide.with(holder.itemView.context).load(message.messageImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.itemSendPhotoImage)
            }else if (message.messageText == "Mặt hàng này còn chứ?"){
                holder.binding.itemSendPhotoImage.visibility = View.VISIBLE
                holder.binding.itemSentTextview.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(message.messageImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.itemSendPhotoImage)
            }
        } else if (holder is ReceiverViewHolder) {
            holder.binding.itemReceiveTextview.text = message.messageText
            if (message.messageText == "Photo" || message.messageText
                == "camera"
            ) {
                holder.binding.itemReceivePhotoImage.visibility = View.VISIBLE
                holder.binding.itemReceiveTextview.visibility = View.GONE
                holder.binding.linearLayout2.setBackgroundResource(R.drawable.border_shadow)
                Glide.with(holder.itemView.context).load(message.messageImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.itemReceivePhotoImage)
            }
            else if (message.messageText == "Mặt hàng này còn chứ?"){
                holder.binding.itemReceivePhotoImage.visibility = View.VISIBLE
                holder.binding.itemReceiveTextview.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(message.messageImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.itemReceivePhotoImage)
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