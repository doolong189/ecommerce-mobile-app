package com.freshervnc.ecommerceapplication.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemHistoryMessageBinding
import com.freshervnc.ecommerceapplication.data.model.Message
import com.freshervnc.ecommerceapplication.utils.Utils

private var onClickItem: ((id: Message, position: Int) -> Unit)? = null

class HistoryMessageAdapter() : RecyclerView.Adapter<HistoryMessageAdapter.HistoryMessageViewHolder>() {

    private var list: List<Message> = listOf()
    fun onClickItemMessage(id: ((id: Message, position: Int) -> Unit)) {
        onClickItem = id
    }

    class HistoryMessageViewHolder(private val binding : ItemHistoryMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun onBind(item: Message) {
            binding.run {
                itemHistoryMessageTvName.text = item.receiverId.name
                Glide.with(itemView.context).load(item.receiverId.image)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(itemHistoryMessageImage)
                itemHistoryMessageTvLastMsg.text = item.lastMsg
                itemHistoryMessageTvLastMsgTime.text = Utils.convertLongToTime(item.lastMsgTime)
                itemView.setOnClickListener {
                    onClickItem?.let {
                        it(item, adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryMessageViewHolder {
        val binding =
            ItemHistoryMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryMessageViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HistoryMessageViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount() = list.size

    fun setMessage(message : List<Message>) {
        list = message
        notifyDataSetChanged()
    }
}