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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ImageSliderAdapter(var group:String, context: Context, sliderImage: ArrayList<String>, userID:String, calID:String) :
    RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {
    private val context: Context = context
    private var sliderImage: ArrayList<String> = arrayListOf()

    val storage= FirebaseStorage.getInstance()
    val storRef = if (group == "1"){
        storage.reference.child("groupcalendar").child(calID)
    }else{
        storage.reference.child(userID).child(calID)
    }



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
        private val mImageView: ImageView = itemView.findViewById(R.id.imageSlider)

        fun bindSliderImage(imageURL: String) {
            storRef.child(imageURL).downloadUrl
                .addOnSuccessListener { imageUri->
                    Glide.with(context)
                        .load(imageUri)
                        .into(mImageView)
                }
        }

    }

    init {
        this.sliderImage = sliderImage
        notifyDataSetChanged()
    }
}