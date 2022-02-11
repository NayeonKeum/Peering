package com.awesomesol.peering.catDiary

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class ShareDiaryRVAdapter(val items: ArrayList<GroupInfo>) : RecyclerView.Adapter<ShareDiaryRVAdapter.Viewholder>()  {

    val storage= FirebaseStorage.getInstance()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShareDiaryRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.catdiary_sharediary_rv_item, parent, false)
        return Viewholder(v)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null


    override fun onBindViewHolder(holder: ShareDiaryRVAdapter.Viewholder, position: Int) {


        holder.bindItems(items[position])

        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: GroupInfo) {
            val groupTitle = itemView.findViewById<TextView>(R.id.tv_catDiaryFragment_groupName)
            val groupNumber = itemView.findViewById<TextView>(R.id.tv_catDiaryFragment_groupNum)
            val groupContainer = itemView.findViewById<ImageView>(R.id.iv_catDiaryFragment_groupContainer)

            groupTitle.text = item.groupName

            var uidNum = item.uidList.count()
            if (uidNum < 10) {
                groupNumber.text = "0" + uidNum.toString()
            } else {
                groupNumber.text = uidNum.toString()
            }

            Glide.with(itemView)
                .load(item.groupImg)
                .into(groupContainer)

        }
    }
}