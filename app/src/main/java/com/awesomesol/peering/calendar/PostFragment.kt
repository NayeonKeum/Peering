package com.awesomesol.peering.calendar

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.awesomesol.peering.R
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.protobuf.StringValue
import com.kakao.sdk.user.UserApiClient
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.util.*
import kotlin.collections.ArrayList


class PostFragment : Fragment() {


    private val TAG="갤러리"
    private lateinit var  galleryRVAdapter: GalleryRVAdapter

    private lateinit var bottomView:View
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<View>


    var fs=Firebase.firestore

    // 슬라이더
    private var sliderViewPager: ViewPager2? = null
    private var layoutIndicator: LinearLayout? = null
    private lateinit var cl_PostFragment: ConstraintLayout
    private lateinit var writePost:ImageView

    private var images:ArrayList<String> = arrayListOf()
    private var curDate:String=""
    private var dateym:String=""
    private var cid:String=""

    private var uid:String=""

    private lateinit var callback:OnBackPressedCallback

    private lateinit var dateGalleryData: ArrayList<HashMap<String, Any>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var bundle = arguments;  //번들 받기. getArguments() 메소드로 받음.
        if (bundle != null){
            // 눌렀을 때 제대로 들어오면!
            // targetDate = bundle.getString("targetDate").toString(); //Name 받기.
            Log.d(TAG, "넘어온 번들: ${bundle.getString("date")}")
            curDate= bundle.getString("date").toString()
            dateym=bundle.getString("dateym").toString()
            cid=bundle.getString("cid").toString()
            try{
                Log.d(TAG, "넘어온 번들: ${bundle.getSerializable("dateGalleryData")}")
                dateGalleryData = bundle.getSerializable("dateGalleryData") as ArrayList<HashMap<String, Any>>
                Log.d(TAG, "넘어온 번들 갤데화: $dateGalleryData")
            } catch(e:NullPointerException){
                dateGalleryData= arrayListOf()
            }
        }

    }
    private fun userCallback(callback:(String)->Unit){
        UserApiClient.instance.me { user, _ ->
            uid = user?.id.toString()
            callback(uid)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_post, container, false)

        view.findViewById<TextView>(R.id.tv_PostFragment_date).text=curDate

        val rv:RecyclerView=view.findViewById<View>(R.id.bottomsheetview).findViewById<RecyclerView>(
            R.id.rv_PostFragment
        )
        Log.d(TAG, rv.toString())
        //rv.layoutManager=GridLayoutManager(context, 3)
        rv.layoutManager=LinearLayoutManager(context).also{it.orientation=LinearLayoutManager.HORIZONTAL}

        userCallback { uid->
            galleryRVAdapter= context?.let { GalleryRVAdapter(it, uid, cid) }!!

            galleryRVAdapter.setView(view)
            //parentFragmentManager
            rv.adapter=galleryRVAdapter


            Log.d(TAG, "targetDate에 있는 사진 개수: " + dateGalleryData.size)
            val datasize: Int? =dateGalleryData.size
            Log.d(TAG+"뭐들었니", dateGalleryData.toString())

            val titleimgs:ArrayList<String> = arrayListOf()
            for (i : Int in 0 until datasize!!){
                Log.d(TAG, "dateGalleryData[i][\"used\"]? ${dateGalleryData[i]["used"]?.javaClass}")
                val ln1:Long=1
                val ln2:Long=2
                // 대표사진(2) 거나 게시 사진(1)이면
                if (dateGalleryData[i]["used"]?.equals(ln1) == true){
                    images.add(dateGalleryData[i]["imageUri"] as String)
                }
                else if (dateGalleryData[i]["used"]?.equals(ln2) == true){
                    titleimgs.add(dateGalleryData[i]["imageUri"] as String)
                }
            }

            galleryRVAdapter.setDataList(dateGalleryData)


            // 바텀 쉿 부착!
            bottomView= view?.findViewById(R.id.ll_PostFragment_bottomsheet)!!
            bottomSheetBehavior= BottomSheetBehavior.from(bottomView as View)

            // 전체 숨김
            // behavior.state = BottomSheetBehavior.STATE_HIDDEN
            // peekHight 만큼
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//
//        inflater.inflate(R.layout.fragment_post_bottomsheet_photos, container).findViewById<ImageView>(
//            R.id.iv_bottomsheet_up
//        ).setOnClickListener {
//            // 전체 보여주기
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        }

            // 둥근 모서리
            cl_PostFragment=view.findViewById(R.id.cl_PostFragment)
            cl_PostFragment.clipToOutline=true

            // 초기에 해주는 거!
            sliderViewPager = view.findViewById(R.id.sliderViewPager)
            layoutIndicator = view.findViewById(R.id.layoutIndicators)

            sliderViewPager!!.offscreenPageLimit = 1

            var allImgs:ArrayList<String> = arrayListOf()
            allImgs.addAll(titleimgs)
            allImgs.addAll(images)

            sliderViewPager!!.adapter = ImageSliderAdapter(requireContext(), allImgs, uid, cid)

            sliderViewPager!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            })
            setupIndicators(allImgs.size)
        }


        //키보드 올라올때 바텀네비케이션 올라오는거 처리 부분
        //setOnFocusChangeListener 로 바텀네비게시연 하이드
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            //r will be populated with the coordinates of your view that area still visible.
            view.getWindowVisibleDisplayFrame(r)
            val heightDiff: Int = view.rootView.height - (r.bottom - r.top)
            if (heightDiff > 500) {
                // if more than 100 pixels, its probably a keyboard...
                activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.GONE
            }
            else{
                activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.VISIBLE
            }
        }
        view.setOnFocusChangeListener { view, b ->
            //Variable 'b' represent whether this view has focus.
            //If b is true, that means "This view is having focus"
            if(b) activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.GONE
            else activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.VISIBLE
            Log.d(TAG, "setOnFocusChangeListener:${b}")
        }

        view.setOnFocusChangeListener { view, b ->
            if(b) activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.GONE
            else activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.VISIBLE
            Log.d(TAG, "setOnFocusChangeListener:${b}")
        }

        view.setOnFocusChangeListener { view, b ->
            if(b) activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.GONE
            else activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.VISIBLE
            Log.d(TAG, "setOnFocusChangeListener:${b}")
        }

        var tempFlag = true
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            //r will be populated with the coordinates of your view that area still visible.
            view.getWindowVisibleDisplayFrame(r)
            val heightDiff: Int = view.rootView.height - (r.bottom - r.top)
            if (heightDiff > 500) {
                // if more than 100 pixels, its probably a keyboard...
                activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.GONE
                tempFlag = false
            }
            else if(tempFlag == false){
                activity?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visibility = View.VISIBLE
                tempFlag = true
            }
        }


        
        // 파베 업뎃
        writePost=view.findViewById(R.id.iv_PostFragment_writePost)

        writePost.setOnClickListener {
            lateinit var hh: HashMap<String, ArrayList<GalleryData>>

            fs.collection("calendars").whereArrayContainsAny("uidList", arrayListOf(uid)).get()
                .addOnSuccessListener { documents->
                    for (document in documents) {
                        if (document.data["cid"].toString().equals(cid)){
                            hh= document.data["dataList4"] as HashMap<String, ArrayList<GalleryData>>
                        }
                    }
                    hh[dateym]=galleryRVAdapter.dateGalleryData as ArrayList<GalleryData>
                    fs.collection("calendars").document(cid).update("dataList4", hh)
                        .addOnSuccessListener { Log.d(TAG, "성공") }
                        .addOnFailureListener{ Log.d(TAG, "실패")}


                }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

            val calendarFragment = CalendarMainFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, calendarFragment).commit()

        }

        return view
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "백프레스 눌름")
                val calendarFragment = CalendarMainFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_screen_panel, calendarFragment)?.commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onResume() {
        super.onResume()
        LayoutInflater.from(context).inflate(R.layout.fragment_post, null, false)
        // sliderViewPager!!.refreshDrawableState()

    }

    // 바텀시트에 리사이클러뷰 어댑터
    class GalleryRVAdapter(var context: Context, uid:String, cid:String):RecyclerView.Adapter<GalleryRVAdapter.ViewHolder>() {

        var dateGalleryData = ArrayList<HashMap<String, Any>>()
        var useImages:ArrayList<String> = arrayListOf()
        var titleImg:ArrayList<String> = arrayListOf()
        //val TAG="갤러리 어댑터"

        lateinit var parentView: View

        lateinit var sliderViewPager:ViewPager2
        lateinit var cl_PostFragment:ConstraintLayout
        lateinit var layoutIndicator:LinearLayout

        val storage= FirebaseStorage.getInstance()
        private lateinit var storRef: StorageReference

        var userID:String = uid
        var calID:String = cid



        internal fun setDataList(dateGalleryData: ArrayList<HashMap<String, Any>>) {
            this.dateGalleryData = dateGalleryData
        }

        // Provide a direct reference to each of the views with data items
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var iv: ImageView
            var addminus:ImageView
            var addtitle:ImageView
            init {
                iv = itemView.findViewById(R.id.img_PostFragment_rv_itemimg)
                addminus=itemView.findViewById(R.id.iv_PostFragment_addminusbtn)
                addtitle=itemView.findViewById(R.id.iv_PostFragment_addtitle)
            }
        }

        fun setView(parentView: View){
            this.parentView = parentView
            sliderViewPager = parentView.findViewById(R.id.sliderViewPager)
            layoutIndicator = parentView.findViewById(R.id.layoutIndicators)
            cl_PostFragment = parentView.findViewById(R.id.cl_PostFragment)
        }

        // Usually involves inflating a layout from XML and returning the holder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryRVAdapter.ViewHolder {
            // Inflate the custom layout
            var view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_post_rv_item,
                parent,
                false
            )
            return ViewHolder(view)
        }

        // Involves populating data into the item through holder
        override fun onBindViewHolder(holder: GalleryRVAdapter.ViewHolder, position: Int) {

            // Get the data model based on position
            var data = dateGalleryData[position]
            val ln0:Long=0
            val ln1:Long=1
            val ln2:Long=2
            if(dateGalleryData[position]["used"]?.equals(ln0) == true){
                holder.addminus.setImageResource(R.drawable.gallery_add)
                holder.addtitle.setImageResource(R.drawable.titlephototrans)
            }
            else if(dateGalleryData[position]["used"]?.equals(ln1) == true){
                holder.addminus.setImageResource(R.drawable.gallery_minus)
                holder.addtitle.setImageResource(R.drawable.titlephototrans)
            }
            else if(dateGalleryData[position]["used"]?.equals(ln2) == true){
                holder.addminus.setImageResource(R.drawable.gallery_minus)
                holder.addtitle.setImageResource(R.drawable.titlephoto)
            }


            // Set item views based on your views and data model
            val uri:String=data.get("imageUri").toString()
            storRef=storage.reference.child(userID).child(calID)
            storRef.child(uri).downloadUrl
                .addOnSuccessListener { imageUri->
                    Glide.with(context)
                        .load(imageUri)
                        .into(holder.iv)
                }

            // [대표 사진]을 눌렀을 때
            holder.addtitle.setOnClickListener {
                val ln0:Long=0
                val ln1:Long=1
                val ln2:Long=2
                if (dateGalleryData[position]["used"]?.equals(ln0)==true){
                    // 아예 추가 안 됨


                    // 현재 대표사진인 친구 0으로 바꾸고
                    removeFromTitle(dateGalleryData)

                    dateGalleryData[position]["used"]=ln2
                    holder.addminus.setImageResource(R.drawable.gallery_minus)
                    holder.addtitle.setImageResource(R.drawable.titlephoto)


                } else if (dateGalleryData[position]["used"]?.equals(ln1)==true){
                    // 대표사진 아님
                    val ln:Long=2

                    // 현재 대표사진인 친구 0으로 바꾸고
                    removeFromTitle(dateGalleryData)

                    dateGalleryData[position]["used"]=ln2
                    holder.addminus.setImageResource(R.drawable.gallery_minus)
                    holder.addtitle.setImageResource(R.drawable.titlephoto)

                }

                notifyDataSetChanged()

                useImages= arrayListOf()
                titleImg= arrayListOf()
                for (i:Int in dateGalleryData.indices){
                    Log.d("뷰페 type", dateGalleryData[i]["used"]?.javaClass.toString())
                    val ln1:Long=1
                    val ln2:Long=2
                    if(dateGalleryData[i]["used"]?.equals(ln1) == true){
                        useImages.add(dateGalleryData[i]["imageUri"].toString())
                    }else if(dateGalleryData[i]["used"]?.equals(ln2) == true){
                        titleImg.add(dateGalleryData[i]["imageUri"].toString())
                    }

                }
                Log.d("뷰페이저 인디1-t", dateGalleryData.toString())
                var allImg:ArrayList<String> = arrayListOf()
                allImg.addAll(titleImg)
                allImg.addAll(useImages)
                Log.d("뷰페이저 인디2-t", allImg.toString())

                // 초기화
                layoutIndicator!!.removeAllViews()

                // 둥근 모서리
                cl_PostFragment.clipToOutline=true

                sliderViewPager!!.adapter = ImageSliderAdapter(context, allImg, userID, calID)
                sliderViewPager!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentIndicator(position)
                    }
                })
                setupIndicators(allImg.size)

            }

            // 눌렀을 때 전달을 그 어댑터에 전달을 해야하네,, 이미지 어댑터..!!
            holder.iv.setOnClickListener {

                val ln:Long=0
                if(dateGalleryData[position]["used"]?.equals(ln) == true){
                    // 사용 안 한 거
                    // 더하는 액션 하고
                    val ln:Long=1
                    dateGalleryData[position]["used"]=ln
                    holder.addminus.setImageResource(R.drawable.gallery_minus)
                }
                else{
                    // 사용 한 거(1, 2)
                    // 빼는 액션하고
                    val ln:Long=0
                    dateGalleryData[position]["used"]=ln
                    holder.addminus.setImageResource(R.drawable.gallery_add)
                }

                notifyDataSetChanged()

                titleImg= arrayListOf()
                useImages= arrayListOf()
                for (i:Int in dateGalleryData.indices){
                    Log.d("뷰페 type", dateGalleryData[i]["used"]?.javaClass.toString())
                    val ln1:Long=1
                    val ln2:Long=2
                    if(dateGalleryData[i]["used"]?.equals(ln1) == true || dateGalleryData[i]["used"]?.equals(ln2) == true){
                        useImages.add(dateGalleryData[i]["imageUri"].toString())
                    } else if(dateGalleryData[i]["used"]?.equals(ln2) == true){
                        titleImg.add(dateGalleryData[i]["imageUri"].toString())
                    }
                }

                Log.d("뷰페이저 인디1", dateGalleryData.toString())

                var allImg:ArrayList<String> = arrayListOf()
                allImg.addAll(titleImg)
                allImg.addAll(useImages)

                Log.d("뷰페이저 인디2", allImg.toString())

                // 초기화
                layoutIndicator!!.removeAllViews()

                // 둥근 모서리
                cl_PostFragment.clipToOutline=true

                sliderViewPager!!.adapter = ImageSliderAdapter(context, allImg, userID, calID)
                sliderViewPager!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentIndicator(position)
                    }
                })
                setupIndicators(allImg.size)

            }
        }
        override fun getItemId(position: Int): Long {
            return super.getItemId(position)
        }
        //  total count of items in the list
        override fun getItemCount() = dateGalleryData.size


        // 현재 대표사진인거 0(미사용)으로 바까주기
        fun removeFromTitle(dateGalleryData:ArrayList<HashMap<String, Any>>){
            for (data in dateGalleryData){
                val ln2:Long=2
                if (data["used"]?.equals(ln2)==true){
                    val ln0:Long=0
                    data["used"]=ln0
                }
            }
        }


        // 중복으로 들어가긴 하는데
        // 뷰페이지 어댑터2
        // 이미지 넘어가는 뷰페이지 어댑터
        private fun setupIndicators(count: Int) {
            val indicators = arrayOfNulls<ImageView>(count)
            Log.d("뷰페이저 indicators 개수", indicators.size.toString())
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(16, 8, 16, 8)
            for (i in indicators.indices) {
                indicators[i] = ImageView(context)
                indicators[i]!!.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.bg_indicator_inactive
                        )
                    }
                )
                indicators[i]!!.layoutParams = params
                layoutIndicator!!.addView(indicators[i])
            }
            setCurrentIndicator(0)
        }

        private fun setCurrentIndicator(position: Int) {
            val childCount = layoutIndicator!!.childCount
            for (i in 0 until childCount) {
                val imageView = layoutIndicator!!.getChildAt(i) as ImageView
                if (i == position) {
                    imageView.setImageDrawable(
                        context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.bg_indicator_active
                            )
                        }
                    )
                } else {
                    imageView.setImageDrawable(
                        context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.bg_indicator_inactive
                            )
                        }
                    )
                }
            }
        }

    }

    // 이미지 넘어가는 뷰페이지 어댑터
    private fun setupIndicators(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 8)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i]!!.setImageDrawable(
                context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.bg_indicator_inactive
                    )
                }
            )
            indicators[i]!!.layoutParams = params
            layoutIndicator!!.addView(indicators[i])
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = layoutIndicator!!.childCount
        for (i in 0 until childCount) {
            val imageView = layoutIndicator!!.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.bg_indicator_active
                        )
                    }
                )
            } else {
                imageView.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.bg_indicator_inactive
                        )
                    }
                )
            }
        }
    }

    override fun onDetach(){
        super.onDetach()
        callback.remove()
    }
}