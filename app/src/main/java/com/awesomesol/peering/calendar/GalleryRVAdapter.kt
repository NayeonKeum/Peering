package com.awesomesol.peering.calendar

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.LayoutInflaterCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import kotlin.coroutines.coroutineContext

class GalleryRVAdapter(var context: Context):RecyclerView.Adapter<GalleryRVAdapter.ViewHolder>() {

    var dataList = emptyList<GalleryData>()
    val TAG="갤러리 어댑터"
    lateinit private var iv_PostFragment:ImageView

    lateinit private var fragmentManager:FragmentManager

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

    fun setFM(fragmentManager:FragmentManager){
        this.fragmentManager=fragmentManager
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryRVAdapter.ViewHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_post_rv_item, parent, false)
        iv_PostFragment=LayoutInflater.from(context).inflate(R.layout.fragment_post, null, false).findViewById(R.id.iv_PostFragment)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: GalleryRVAdapter.ViewHolder, position: Int) {

        // Get the data model based on position
        var data = dataList[position]

        // Set item views based on your views and data model
        holder.iv.setImageURI(data.imageUri)

        holder.iv.setOnClickListener {
            var iv_PostFragment=LayoutInflater.from(context).inflate(R.layout.fragment_post, null, false).findViewById<ImageView>(R.id.iv_PostFragment)
            iv_PostFragment.setImageURI(data.imageUri)
            Log.d(TAG, "이미지뷰 찾음? $iv_PostFragment")

            val frg= fragmentManager.findFragmentById(R.id.fragment_post);
            val ft = fragmentManager.beginTransaction();
            // Log.d(TAG, frg.toString())
            if (frg != null) {
                Log.d(TAG, "프랙 찾음")
                ft.detach(frg)
                ft.attach(frg);
                ft.commit();
            };



//            fun ViewGroup.inflate(resId: Int, attach: Boolean = false): View
//                    = LayoutInflater.from(context).inflate(R.layout.fragment_post, this, false)
        }

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}