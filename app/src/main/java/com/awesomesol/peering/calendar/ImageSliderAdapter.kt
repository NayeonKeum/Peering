package com.awesomesol.peering.calendar

import android.content.Context
import com.awesomesol.peering.R
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target


class ImageSliderAdapter(context: Context, sliderImage: Array<String>) :
    RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {
    private val context: Context
    private val sliderImage: Array<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slider, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindSliderImage(sliderImage[position])
    }

    override fun getItemCount(): Int {
        return sliderImage.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mImageView: ImageView
        fun bindSliderImage(imageURL: String?) {
            Glide.with(context)
                .load(imageURL)
                .into(mImageView)
        }

        init {
            mImageView = itemView.findViewById(R.id.imageSlider)
        }
    }

    init {
        this.context = context
        this.sliderImage = sliderImage
    }
}