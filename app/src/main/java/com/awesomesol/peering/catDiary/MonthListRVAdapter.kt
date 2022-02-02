package com.awesomesol.peering.catDiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R

class MonthListRVAdapter(val items: ArrayList<String>) : RecyclerView.Adapter<MonthListRVAdapter.Viewholder>() {
    // 아이템 하나(month_list_rv_item.xml) 갖고와서 하나의 레이아웃 만들어줌
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthListRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.catdiary_month_list_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: MonthListRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // month_list_rv_item.xml에 하나하나씩 리턴 넣어주는 역할. 아이템의 내용물 넣어주기
    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: String) {
            val month = itemView.findViewById<TextView>(R.id.textMonth)
            month.text = item
        }
    }
}