package com.awesomesol.peering.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.bumptech.glide.Glide

class FeedRVAdapter(val items : ArrayList<FeedModel>) : RecyclerView.Adapter<FeedRVAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: FeedRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])

    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item : FeedModel){

            // recyclerview click할 경우 DiaryReadFragment로 넘어가게 하기 위한 코드
            itemView.setOnClickListener {
                val diaryreadFragment = DiaryReadFragment()

                /* feedfragment에서 전달해줘야 함
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_screen_panel, diaryreadFragment).commitNow()*/
            }

            val name = itemView.findViewById<TextView>(R.id.tv_FeedRVItem_nickname)
            name.text = item.nickname
            val content = itemView.findViewById<TextView>(R.id.tv_FeedRVItem_content)
            content.text = item.content
            val ivprofile = itemView.findViewById<ImageView>(R.id.iv_FeedRVItem_profileImg)
            Glide.with(itemView).load(R.drawable.feed_profile).circleCrop().into(ivprofile)
        }
    }
}