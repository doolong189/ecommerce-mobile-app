package com.freshervnc.ecommerceapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.data.model.UserInfo


class UserAdapter (
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var users: List<UserInfo> = listOf()
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProfile : ImageView = itemView.findViewById(R.id.item_asu_imageProfile)
        val tvName : TextView = itemView.findViewById(R.id.item_asu_tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_status_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        if (user.image == "No image") {
            holder.imageProfile.setImageResource(R.drawable.ic_launcher_foreground)
        }else{
            Glide.with(holder.itemView.context)
                .load(user.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageProfile)
        }
        holder.tvName.text = user.name
        val animation =
            AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.slide_in_left)
        holder.itemView.startAnimation(animation)

    }

    override fun getItemCount() = users.size

    fun submitList(users : List<UserInfo>) {
        this.users = users
        notifyDataSetChanged()
    }
}