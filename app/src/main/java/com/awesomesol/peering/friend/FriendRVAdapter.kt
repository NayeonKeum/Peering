package com.awesomesol.peering.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.bumptech.glide.Glide

class FriendRVAdapter(val items : ArrayList<FriendModel>) : RecyclerView.Adapter<FriendRVAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.friend_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: FriendRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){
        // friend_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FriendModel){
            val name = itemView.findViewById<TextView>(R.id.tv_FriendRVItem_nickname)
            name.text = item.nickname
            val email = itemView.findViewById<TextView>(R.id.tv_FriendRVItem_email)
            email.text = item.email
            val profileImg = itemView.findViewById<ImageView>(R.id.iv_FriendRVItem_profileImg)
            Glide.with(itemView).load(R.drawable.feed_profile).circleCrop().into(profileImg)

        }
    }
}