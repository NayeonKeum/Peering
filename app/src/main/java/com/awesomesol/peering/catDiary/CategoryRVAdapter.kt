package com.awesomesol.peering.catDiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R

class CategoryRVAdapter(val items: ArrayList<CategoryInfo>) : RecyclerView.Adapter<CategoryRVAdapter.Viewholder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.catdiary_category_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: CategoryRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: CategoryInfo) {
            val categoryTitle = itemView.findViewById<TextView>(R.id.tv_catDiaryFragment_catSub)
            categoryTitle.text = item.categoryList.toString()

        }
    }
}