package com.freshervnc.ecommerceapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ItemOrderBinding
import com.freshervnc.ecommerceapplication.data.model.Notification
import com.freshervnc.ecommerceapplication.data.model.Order
import com.freshervnc.ecommerceapplication.utils.Utils
private var onClickItem: ((id: Order, position: Int) -> Unit)? = null

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private var list: List<Order> = listOf()
    fun onClickItemOrder(id: ((id: Order, position: Int) -> Unit)) {
        onClickItem = id
    }
    class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(item: Order) {
            binding.run {
                itemOrderTvOrderCode.text = "Mã đơn hàng: ${item._id}"
                itemOrderTvDate.text = "Ngày: ${item.date}"
                itemOrderTvTotalPrice.text = "Tổng tiền: ${Utils.formatPrice(item.totalPrice)} đ"
                if (item.receiptStatus == 0) {
                    itemOrderTvStatus.setTextColor(binding.root.resources.getColor(R.color.text))
                    itemOrderTvStatus.text = "Trạng thái: ${processStatus}"
                }else if(item.receiptStatus == 1){
                    itemOrderTvStatus.setTextColor(binding.root.resources.getColor(R.color.completed))
                    itemOrderTvStatus.text = "Trạng thái: ${completedStatus}"
                }else{
                    itemOrderTvStatus.setTextColor(binding.root.resources.getColor(R.color.cancel))
                    itemOrderTvStatus.text = "Trạng thái: ${cancelStatus}"
                }
                itemView.setOnClickListener {
                    onClickItem?.let {
                        it(item, adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun submitList(orders: List<Order>) {
        list = orders
        notifyDataSetChanged()
    }

    companion object {
        val processStatus = "Đang xử lý"
        val completedStatus = "Đã giao"
        val cancelStatus = "Đã hủy"
    }
}