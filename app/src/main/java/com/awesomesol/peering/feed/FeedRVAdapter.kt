package com.awesomesol.peering.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R

class FeedRVAdapter(val item : ArrayList<String>) : RecyclerView.Adapter<FeedRVAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: FeedRVAdapter.Viewholder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item : String){

        }
    }
}