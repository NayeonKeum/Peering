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

    val storage = FirebaseStorage.getInstance()
    val ln0:Long=0
    val ln1:Long=1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View?
        return when (viewType) {
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
    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    // onCreateViewHolder에서 가져와서 view에 실제 데이터 연결
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Item클릭을 위한 추가
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
        when (items[position].type.toInt()) {
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
    }

    // item의 총 갯수
    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int):Int {
        return items[position].type.toInt()
    }

    inner class MultiViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cid: String = ""
        var uid: String = ""
        var nickname: String = ""
        var mainImg: ArrayList<HashMap<String, Any>> = arrayListOf()
        var profileImg: String = ""
        var content: String = ""
        var publicScope: Long = 0
        var category: String = ""
        var date: String = ""
        var type: Long = 0
        var isGroup: Long = 0
        lateinit var storRef: StorageReference

        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FeedModel) {
            cid = item.cid
            uid = item.uid
            content = item.content
            profileImg = item.profileImg
            mainImg = item.mainImg
            isGroup = item.isGroup
            date=item.date
            itemView.findViewById<TextView>(R.id.tv_FeedRVItem_date).text=date

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
            val iv_FeedRVItem_mainImg =
                itemView.findViewById<ImageView>(R.id.iv_FeedRVItem1_mainImg)

            if (isGroup == ln0) { storRef=storage.reference.child(uid).child(cid)}
            else if(isGroup==ln1) {storRef=storage.reference.child("groupcalendar").child(cid)}


            storRef.child(mainImg[0]["imageUri"] as String).downloadUrl
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

    inner class MultiViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cid: String = ""
        var uid: String = ""
        var nickname: String = ""
        var mainImg: ArrayList<HashMap<String, Any>> = arrayListOf()
        var profileImg: String = ""
        var content: String = ""
        var publicScope: Long = 0
        var category: String = ""
        var date: String = ""
        var type: Long = 0
        var isGroup: Long = 0
        lateinit var storRef: StorageReference


        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FeedModel) {
            cid = item.cid
            uid = item.uid
            content = item.content
            profileImg = item.profileImg
            mainImg = item.mainImg
            isGroup = item.isGroup
            date=item.date
            itemView.findViewById<TextView>(R.id.tv_FeedRVItem_date).text=date

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
            val iv_FeedRVItem_mainImg =
                itemView.findViewById<ImageView>(R.id.iv_FeedRVItem2_mainImg)
            val iv_FeedRVItem2_mainImg2 =
                itemView.findViewById<ImageView>(R.id.iv_FeedRVItem2_mainImg2)


            if (isGroup == ln0) { storRef=storage.reference.child(uid).child(cid)}
            else if(isGroup==ln1) {storRef=storage.reference.child("groupcalendar").child(cid)}


            storRef.child(mainImg[0]["imageUri"] as String).downloadUrl
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
            storRef.child(mainImg[1]["imageUri"] as String).downloadUrl
                .addOnSuccessListener { imageUri ->
                    Glide.with(itemView)
                        .load(imageUri)
                        .into(iv_FeedRVItem2_mainImg2);
                }
                .addOnFailureListener {
                    Glide.with(itemView)
                        .load(R.drawable.feed_main_img)
                        .into(iv_FeedRVItem2_mainImg2);
                }
        }
    }
    inner class MultiViewHolder3(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var cid: String = ""
        var uid: String = ""
        var nickname: String = ""
        var mainImg:ArrayList<HashMap<String, Any>> = arrayListOf()
        var profileImg: String = ""
        var content: String = ""
        var publicScope:Long=0
        var category:String=""
        var date:String=""
        var type : Long = 0
        var isGroup:Long=0
        lateinit var storRef:StorageReference



        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FeedModel) {
            cid = item.cid
            uid = item.uid
            content = item.content
            profileImg = item.profileImg
            mainImg = item.mainImg
            isGroup=item.isGroup
            date=item.date
            itemView.findViewById<TextView>(R.id.tv_FeedRVItem_date).text=date

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
            val iv_FeedRVItem3_Img=itemView.findViewById<ImageView>(R.id.iv_FeedRVItem3_Img)
            val iv_FeedRVItem3_Img2=itemView.findViewById<ImageView>(R.id.iv_FeedRVItem3_Img2)



            if (isGroup == ln0) { storRef=storage.reference.child(uid).child(cid)}
            else if(isGroup==ln1) {storRef=storage.reference.child("groupcalendar").child(cid)}


            storRef.child(mainImg[0]["imageUri"] as String).downloadUrl
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
            storRef.child(mainImg[1]["imageUri"] as String).downloadUrl
                .addOnSuccessListener { imageUri ->
                    Glide.with(itemView)
                        .load(imageUri)
                        .into(iv_FeedRVItem3_Img);
                }
                .addOnFailureListener {
                    Glide.with(itemView)
                        .load(R.drawable.feed_main_img)
                        .into(iv_FeedRVItem3_Img);
                }
            storRef.child(mainImg[2]["imageUri"] as String).downloadUrl
                .addOnSuccessListener { imageUri ->
                    Glide.with(itemView)
                        .load(imageUri)
                        .into(iv_FeedRVItem3_Img2);
                }
                .addOnFailureListener {
                    Glide.with(itemView)
                        .load(R.drawable.feed_main_img)
                        .into(iv_FeedRVItem3_Img2);
                }
        }
    }
    inner class MultiViewHolder4(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var cid: String = ""
        var uid: String = ""
        var nickname: String = ""
        var mainImg:ArrayList<HashMap<String, Any>> = arrayListOf()
        var profileImg: String = ""
        var content: String = ""
        var publicScope:Long=0
        var category:String=""
        var date:String=""
        var type : Long = 0
        var isGroup:Long=0
        lateinit var storRef:StorageReference



        // feed_rv_item의 item의 값들을 하나하나 넣어주는 코드
        fun bindItems(item: FeedModel) {
            cid = item.cid
            uid = item.uid
            content = item.content
            profileImg = item.profileImg
            mainImg = item.mainImg
            isGroup=item.isGroup
            date=item.date
            itemView.findViewById<TextView>(R.id.tv_FeedRVItem_date).text=date

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
            val iv_FeedRVItem_Img = itemView.findViewById<ImageView>(R.id.iv_FeedRVItem_Img)


            if (isGroup == ln0) { storRef=storage.reference.child(uid).child(cid)}
            else if(isGroup==ln1) {storRef=storage.reference.child("groupcalendar").child(cid)}


            storRef.child(mainImg[0]["imageUri"] as String).downloadUrl
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
            storRef.child(mainImg[1]["imageUri"] as String).downloadUrl
                .addOnSuccessListener { imageUri ->
                    Glide.with(itemView)
                        .load(imageUri)
                        .into(iv_FeedRVItem_Img);
                }
                .addOnFailureListener {
                    Glide.with(itemView)
                        .load(R.drawable.feed_main_img)
                        .into(iv_FeedRVItem_Img);
                }
        }
    }
}