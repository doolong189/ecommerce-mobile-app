package com.freshervnc.ecommerceapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.data.enity.Product
import com.freshervnc.ecommerceapplication.databinding.ItemCartBinding
import com.freshervnc.ecommerceapplication.databinding.ItemCategoryBinding
import com.freshervnc.ecommerceapplication.model.Cart
import com.freshervnc.ecommerceapplication.model.Category
import com.freshervnc.ecommerceapplication.utils.Utils

private var onClickItemAddQuantity: ((id: Product, quantity: Int) -> Unit)? = null
private var onClickItemSubQuantity: ((id: Product, quantity: Int) -> Unit)? = null
private var onClickItemDelete: ((id: Product, position: Int) -> Unit)? = null
private var onUpdateTotal : ((total : Int , amount : Int) -> Unit)? = null
class CartAdapter() : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private var list: List<Product> = listOf()

    fun onClickItemAddQuantity(id: ((item : Product, position: Int) -> Unit)) {
        onClickItemAddQuantity = id
    }
    fun onClickItemSubQuantity(id: ((item : Product, position: Int) -> Unit)) {
        onClickItemSubQuantity = id
    }
    fun onClickItemDelete(id: ((item : Product, position: Int) -> Unit)) {
        onClickItemDelete = id
    }

    fun onUpdateTotal(id : ((total : Int , amount : Int) -> Unit)){
        onUpdateTotal = id
    }

    private var selectedItems: MutableSet<Int> = mutableSetOf()

    class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Product) {
            binding.run {
                Glide.with(binding.root.context)
                    .load(item.image)
                    .placeholder(R.drawable.logo_app)
                    .into(itemCartImgView)
                itemCartTvName.text = item.name
                itemCartTvPrice.text = "${Utils.formatPrice(item.price!!)} đ"
                itemCartTvQuantity.text = "${item.quantity}"

                itemCartImgAdd.setOnClickListener {
                    item.quantity = item.quantity!! + 1
                    binding.itemCartTvQuantity.text = item.quantity.toString()
                    binding.itemCartTvQuantity.text = item.quantity.toString()
                    onClickItemAddQuantity?.let {
                        it(item, item.quantity!!)
                    }
                }

                itemCartImgSub.setOnClickListener {
                    if (item.quantity == 0) {
                        item.quantity = 0
                    } else {
                        item.quantity = item.quantity!! - 1
                    }
                    onClickItemSubQuantity?.let {
                        it(item, adapterPosition)
                    }
                }

                itemCartImgDelete.setOnClickListener {
                    onClickItemDelete?.let {
                        it(item, adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun submitList(carts: List<Product>) {
        list = carts
        notifyDataSetChanged()
    }

     fun updateTotalPrice() {
        var total = 0
        var amount = 0
        for (index in selectedItems) {
            val item = list[index]
            total +=  item.quantity ?: 0
            amount += item.quantity ?: 0
        }
         onUpdateTotal?.let {
             it(total , amount)
         }
    }
}