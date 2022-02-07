package com.awesomesol.peering.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.bumptech.glide.Glide

class FeedRVAdapter(val items : ArrayList<FeedModel>) : RecyclerView.Adapter<FeedRVAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
        return Viewholder(v)
    }

    // Item 클릭을 위한 추가
    interface ItemClick{
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    // onCreateViewHolder에서 가져와서 view에 실제 데이터 연결
    override fun onBindViewHolder(holder: FeedRVAdapter.Viewholder, position: Int) {
        // Item클릭을 위한 추가
        if(itemClick != null){
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position])
    }

    // item의 총 갯수
    override fun getItemCount(): Int {
        return items.size
    }
    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item : FeedModel){
            val date = itemView.findViewById<TextView>(R.id.tv_FeedRVItem_date)
            date.text = item.date
            val name = itemView.findViewById<TextView>(R.id.tv_FeedRVItem_nickname)
            name.text = item.uid
            val content = itemView.findViewById<TextView>(R.id.tv_FeedRVItem_content)
            content.text = item.content
            val ivprofile = itemView.findViewById<ImageView>(R.id.iv_FeedRVItem_profileImg)
            Glide.with(itemView).load(R.drawable.feed_profile).circleCrop().into(ivprofile)
        }
    }
}