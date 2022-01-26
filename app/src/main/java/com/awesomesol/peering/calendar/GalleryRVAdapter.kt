package com.awesomesol.peering.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R

class GalleryRVAdapter(var context: Context):RecyclerView.Adapter<GalleryRVAdapter.ViewHolder>() {

    var dataList = emptyList<GalleryData>()

    internal fun setDataList(dataList: List<GalleryData>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv: ImageView
        init {
            iv = itemView.findViewById(R.id.img_PostFragment_rv_itemimg)
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryRVAdapter.ViewHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_post_rv_item, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: GalleryRVAdapter.ViewHolder, position: Int) {

        // Get the data model based on position
        var data = dataList[position]

        // Set item views based on your views and data model
        holder.iv.setImageURI(data.imageUri)
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}