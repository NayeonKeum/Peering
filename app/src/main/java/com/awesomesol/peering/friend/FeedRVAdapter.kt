package com.awesomesol.peering.friend

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FeedRVAdapter(val items : ArrayList<FeedModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val storage= FirebaseStorage.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*
        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
        return Viewholder(v)*/
        val view : View?
        return when(viewType){
            multi_type1 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.feed_rv_item1,
                    parent,
                    false
                )
                MultiViewHolder1(view)
            }
            multi_type2 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.feed_rv_item2,
                    parent,
                    false
                )
                MultiViewHolder2(view)
            }
            multi_type3 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.feed_rv_item3,
                    parent,
                    false
                )
                MultiViewHolder3(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.feed_rv_item,
                    parent,
                    false
                )
                MultiViewHolder4(view)
            }
        }
    }

    // Item 클릭을 위한 추가
    interface ItemClick{
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    // onCreateViewHolder에서 가져와서 view에 실제 데이터 연결
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Item클릭을 위한 추가
        if(itemClick != null){
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
        when(items[position].type){
            multi_type1 -> {
                (holder as MultiViewHolder1).bindItems(items[position])
                holder.setIsRecyclable(false)
            }
            multi_type2 -> {
                (holder as MultiViewHolder2).bindItems(items[position])
                holder.setIsRecyclable(false)
            }
            multi_type3 -> {
                (holder as MultiViewHolder3).bindItems(items[position])
                holder.setIsRecyclable(false)
            }
            else -> {
                (holder as MultiViewHolder4).bindItems(items[position])
                holder.setIsRecyclable(false)
            }
        }
        // holder.bindItems(items[position])
    }

    // item의 총 갯수
    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }
    inner class MultiViewHolder1(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var cid: String = ""
        var uid: String = ""
        var nickname: String = ""
        var mainImg: String = ""
        var profileImg: String = ""
        var content: String = ""
        val type : Int = 0

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FeedModel) {
            cid = item.cid
            uid = item.uid
            content = item.content
            profileImg = item.profileImg
            mainImg = item.mainImg


            val tv_FeedRVItem_nickname =
                itemView.findViewById<TextView>(R.id.tv_FeedRVItem1_nickname)
            tv_FeedRVItem_nickname.text = item.nickname
            val tv_FeedRVItem_content = itemView.findViewById<TextView>(R.id.tv_FeedRVItem1_content)
            tv_FeedRVItem_content.text = content

            val iv_FeedRVItem_profileImg =
                itemView.findViewById<ImageView>(R.id.iv_FeedRVItem1_profileImg)
            Glide.with(itemView)
                .load(profileImg)
                .circleCrop()
                .into(iv_FeedRVItem_profileImg)
            val iv_FeedRVItem_mainImg = itemView.findViewById<ImageView>(R.id.iv_FeedRVItem1_mainImg)

            storage.reference.child(uid).child(cid).child(mainImg).downloadUrl
                .addOnSuccessListener { imageUri ->
                    Glide.with(itemView)
                        .load(imageUri)
                        .into(iv_FeedRVItem_mainImg);
                }
                .addOnFailureListener {
                    Glide.with(itemView)
                        .load(R.drawable.feed_main_img)
                        .into(iv_FeedRVItem_mainImg);
                }
        }
    }
    inner class MultiViewHolder2(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var cid: String = ""
        var uid: String = ""
        var nickname: String = ""
        var mainImg: String = ""
        var profileImg: String = ""
        var content: String = ""
        val type : Int = 0

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FeedModel) {
            cid = item.cid
            uid = item.uid
            content = item.content
            profileImg = item.profileImg
            mainImg = item.mainImg


            val tv_FeedRVItem_nickname =
                itemView.findViewById<TextView>(R.id.tv_FeedRVItem2_nickname)
            tv_FeedRVItem_nickname.text = item.nickname
            val tv_FeedRVItem_content = itemView.findViewById<TextView>(R.id.tv_FeedRVItem2_content)
            tv_FeedRVItem_content.text = content

            val iv_FeedRVItem_profileImg =
                itemView.findViewById<ImageView>(R.id.iv_FeedRVItem2_profileImg)
            Glide.with(itemView)
                .load(profileImg)
                .circleCrop()
                .into(iv_FeedRVItem_profileImg)
            val iv_FeedRVItem_mainImg = itemView.findViewById<ImageView>(R.id.iv_FeedRVItem2_mainImg)

            storage.reference.child(uid).child(cid).child(mainImg).downloadUrl
                .addOnSuccessListener { imageUri ->
                    Glide.with(itemView)
                        .load(imageUri)
                        .into(iv_FeedRVItem_mainImg);
                }
                .addOnFailureListener {
                    Glide.with(itemView)
                        .load(R.drawable.feed_main_img)
                        .into(iv_FeedRVItem_mainImg);
                }
        }
    }
    inner class MultiViewHolder3(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var cid: String = ""
        var uid: String = ""
        var nickname: String = ""
        var mainImg: String = ""
        var profileImg: String = ""
        var content: String = ""
        val type : Int = 0

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FeedModel) {
            cid = item.cid
            uid = item.uid
            content = item.content
            profileImg = item.profileImg
            mainImg = item.mainImg


            val tv_FeedRVItem_nickname =
                itemView.findViewById<TextView>(R.id.tv_FeedRVItem3_nickname)
            tv_FeedRVItem_nickname.text = item.nickname
            val tv_FeedRVItem_content = itemView.findViewById<TextView>(R.id.tv_FeedRVItem3_content)
            tv_FeedRVItem_content.text = content

            val iv_FeedRVItem_profileImg =
                itemView.findViewById<ImageView>(R.id.iv_FeedRVItem3_profileImg)
            Glide.with(itemView)
                .load(profileImg)
                .circleCrop()
                .into(iv_FeedRVItem_profileImg)
            val iv_FeedRVItem_mainImg = itemView.findViewById<ImageView>(R.id.iv_FeedRVItem3_mainImg)

            storage.reference.child(uid).child(cid).child(mainImg).downloadUrl
                .addOnSuccessListener { imageUri ->
                    Glide.with(itemView)
                        .load(imageUri)
                        .into(iv_FeedRVItem_mainImg);
                }
                .addOnFailureListener {
                    Glide.with(itemView)
                        .load(R.drawable.feed_main_img)
                        .into(iv_FeedRVItem_mainImg);
                }
        }
    }
    inner class MultiViewHolder4(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var cid: String = ""
        var uid: String = ""
        var nickname: String = ""
        var mainImg: String = ""
        var profileImg: String = ""
        var content: String = ""
        val type : Int = 0

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FeedModel) {
            cid = item.cid
            uid = item.uid
            content = item.content
            profileImg = item.profileImg
            mainImg = item.mainImg


            val tv_FeedRVItem_nickname =
                itemView.findViewById<TextView>(R.id.tv_FeedRVItem_nickname)
            tv_FeedRVItem_nickname.text = item.nickname
            val tv_FeedRVItem_content = itemView.findViewById<TextView>(R.id.tv_FeedRVItem_content)
            tv_FeedRVItem_content.text = content

            val iv_FeedRVItem_profileImg =
                itemView.findViewById<ImageView>(R.id.iv_FeedRVItem_profileImg)
            Glide.with(itemView)
                .load(profileImg)
                .circleCrop()
                .into(iv_FeedRVItem_profileImg)
            val iv_FeedRVItem_mainImg = itemView.findViewById<ImageView>(R.id.iv_FeedRVItem_mainImg)

            storage.reference.child(uid).child(cid).child(mainImg).downloadUrl
                .addOnSuccessListener { imageUri ->
                    Glide.with(itemView)
                        .load(imageUri)
                        .into(iv_FeedRVItem_mainImg);
                }
                .addOnFailureListener {
                    Glide.with(itemView)
                        .load(R.drawable.feed_main_img)
                        .into(iv_FeedRVItem_mainImg);
                }
        }
    }
    /*
    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var cid:String=""
        var uid:String=""
        var nickname : String = ""
        var mainImg : String = ""
        var profileImg : String = ""
        var content : String = ""

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item : FeedModel){
            cid= item.cid
            uid=item.uid
            content=item.content
            profileImg=item.profileImg
            mainImg=item.mainImg


            val tv_FeedRVItem_nickname = itemView.findViewById<TextView>(R.id.tv_FeedRVItem_nickname)
            tv_FeedRVItem_nickname.text = item.nickname
            val tv_FeedRVItem_content = itemView.findViewById<TextView>(R.id.tv_FeedRVItem_content)
            tv_FeedRVItem_content.text = content

            val iv_FeedRVItem_profileImg = itemView.findViewById<ImageView>(R.id.iv_FeedRVItem_profileImg)
            Glide.with(itemView)
                    .load(profileImg)
                    .circleCrop()
                    .into(iv_FeedRVItem_profileImg)
            val iv_FeedRVItem_mainImg=itemView.findViewById<ImageView>(R.id.iv_FeedRVItem_mainImg)

             storage.reference.child(uid).child(cid).child(mainImg).downloadUrl
                    .addOnSuccessListener { imageUri->
                        Glide.with(itemView)
                                .load(imageUri)
                                .into(iv_FeedRVItem_mainImg);
                    }
                     .addOnFailureListener {
                         Glide.with(itemView)
                                 .load(R.drawable.feed_main_img)
                                 .into(iv_FeedRVItem_mainImg);
                     }
        }
    }*/
}