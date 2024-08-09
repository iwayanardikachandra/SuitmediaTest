package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.model.DataItem

class UserAdapter(
    private var userList: MutableList<DataItem>,
    private val onUserClick: (DataItem) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, onUserClick)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun addUsers(newUsers: List<DataItem>) {
        userList.addAll(newUsers)
        notifyItemRangeInserted(userList.size - newUsers.size, newUsers.size)
    }

    fun setUsers(newUsers: List<DataItem>) {
        userList.clear()
        userList.addAll(newUsers)
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView: ImageView = itemView.findViewById(R.id.image)
        private val firstNameTextView: TextView = itemView.findViewById(R.id.firstname)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.lastname)
        private val emailTextView: TextView = itemView.findViewById(R.id.email)

        fun bind(user: DataItem, onUserClick: (DataItem) -> Unit) {
            firstNameTextView.text = user.firstName
            lastNameTextView.text = user.lastName
            emailTextView.text = user.email

            Glide.with(itemView.context)
                .load(user.avatar)
                .circleCrop()
                .into(avatarImageView)

            itemView.setOnClickListener {
                onUserClick(user)
            }
        }
    }
}
