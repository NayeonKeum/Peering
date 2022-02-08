package com.awesomesol.peering.catDiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R

class ShareDiaryRVAdapter(val items: ArrayList<String>) : RecyclerView.Adapter<ShareDiaryRVAdapter.Viewholder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShareDiaryRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.catdiary_sharediary_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: ShareDiaryRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: String) {
            val groupTitle = itemView.findViewById<TextView>(R.id.tv_catDiaryFragment_groupName)
            groupTitle.text = item

        }
    }
}