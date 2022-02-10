package com.awesomesol.peering.catDiary

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.awesomesol.peering.friend.FeedModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GroupCalPostFragment : Fragment() {


    private val TAG="갤러리"
    private lateinit var  galleryRVAdapter: GalleryRVAdapter

    private lateinit var bottomView:View
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<View>


    var fs=Firebase.firestore
    val storage=FirebaseStorage.getInstance()
    lateinit var storRef:StorageReference


    // 슬라이더
    private var sliderViewPager: ViewPager2? = null
    private var layoutIndicator: LinearLayout? = null
    private lateinit var cl_PostFragment: ConstraintLayout
    private lateinit var writePost:ImageView
    lateinit var tv_PostFragment_content:TextView
    lateinit var spn_PostFragment_publicScope:Spinner
    lateinit var spn_PostFragment_category:Spinner



    private var images:ArrayList<String> = arrayListOf()
    private var curDate:String=""
    private var dateym:String=""
    private var cid:String=""
    private var uid:String=""
    private var nickname:String=""
    private var profileImagePath=""

    private var categories:HashMap<String, ArrayList<String>> = hashMapOf()


    private lateinit var callback:OnBackPressedCallback

    private lateinit var dateGalleryData: ArrayList<HashMap<String, Any>>
    private lateinit var dataList_fromGaL: ArrayList<HashMap<String, Any>>
    private var content:String=""

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
            content=bundle.getString("content").toString()
            uid=bundle.getString("uid").toString()
            nickname=bundle.getString("nickname").toString()
            profileImagePath=bundle.getString("profileImagePath").toString()


            storRef=storage.reference.child(uid).child(cid)

            try{
                dateGalleryData = bundle.getSerializable("dateGalleryData") as ArrayList<HashMap<String, Any>>
                Log.d(TAG, "넘어온 번들 갤데화1: $dateGalleryData")
            } catch(e:NullPointerException){
                Log.d(TAG, "넘어온 번들1 null")
                dateGalleryData = arrayListOf()
            }
            try{
                dataList_fromGaL=bundle.getSerializable("dataList_fromGaL") as ArrayList<HashMap<String, Any>>
                Log.d(TAG, "넘어온 번들 갤데화2: $dataList_fromGaL")
            } catch(e:NullPointerException){
                Log.d(TAG, "넘어온 번들2 null")
                dataList_fromGaL = arrayListOf()
            }

        }

    }

    fun storageCallback(callback:(ArrayList<HashMap<String, Any>>)->Unit){
        // HashMap<String, ArrayList<HashMap<String, Any>>>
        // 그외
        val imgsOfDate: ArrayList<HashMap<String, Any>>? = dataList_fromGaL
        if (imgsOfDate != null) {
            for (img in imgsOfDate){
                val imgUri:String= img["imageUri"] as String
                val fileName="myCal"+Random().nextInt(100000)
                img["imageUri"]=fileName
                storage.reference.child("groupcalendar").child(cid).child(fileName)
                    .putFile(imgUri.toUri())
                    .addOnSuccessListener {}
                    .addOnFailureListener{}

            }
            dataList_fromGaL=imgsOfDate
            callback(imgsOfDate)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_post, container, false)

        view.findViewById<TextView>(R.id.tv_PostFragment_date).text=curDate
        tv_PostFragment_content=view.findViewById(R.id.tv_PostFragment_content)
//        tv_PostFragment_publicScope=view.findViewById(R.id.tv_PostFragment_publicScope)
//        tv_PostFragment_category=view.findViewById(R.id.tv_PostFragment_category)
        spn_PostFragment_publicScope=view.findViewById(R.id.spn_PostFragment_publicScope)
        spn_PostFragment_category=view.findViewById(R.id.spn_PostFragment_category)


        if (content==null){
            content=""
        }
        tv_PostFragment_content.text=content



        val rv:RecyclerView=view.findViewById<View>(R.id.bottomsheetview).findViewById<RecyclerView>(
            R.id.rv_PostFragment
        )
        Log.d(TAG, rv.toString())
        //rv.layoutManager=GridLayoutManager(context, 3)
        rv.layoutManager=LinearLayoutManager(context).also{it.orientation=LinearLayoutManager.HORIZONTAL}

        galleryRVAdapter= context?.let { GalleryRVAdapter(it, uid, cid) }!!

        galleryRVAdapter.setView(view)
        //parentFragmentManager
        rv.adapter=galleryRVAdapter


        var groupNuserGalData:ArrayList<HashMap<String, Any>> = arrayListOf()
        groupNuserGalData.addAll(dateGalleryData)

        storageCallback {
            if(dateGalleryData.size!=0 && dataList_fromGaL.size!=0){
                dataList_fromGaL[0]["used"]=0
            }
            groupNuserGalData.addAll(dataList_fromGaL)

            Log.d(TAG, "사진 개수: " + groupNuserGalData.size)
            val datasize: Int? =groupNuserGalData.size
            Log.d(TAG+"뭐들었니", groupNuserGalData.toString())

            val titleimgs:ArrayList<String> = arrayListOf()
            for (i : Int in 0 until datasize!!){
                val ln1:Long=1
                val ln2:Long=2
                // 대표사진(2) 거나 게시 사진(1)이면
                if (groupNuserGalData[i]["used"]?.equals(ln1) == true){
                    images.add(groupNuserGalData[i]["imageUri"] as String)
                }
                else if (groupNuserGalData[i]["used"]?.equals(ln2) == true){
                    titleimgs.add(groupNuserGalData[i]["imageUri"] as String)
                }
            }

            Log.d(TAG, "groupNuserGalData, $groupNuserGalData")
            galleryRVAdapter.setDataList(groupNuserGalData)


            // 바텀 쉿 부착!
            bottomView= view?.findViewById(R.id.ll_PostFragment_bottomsheet)!!
            bottomSheetBehavior= BottomSheetBehavior.from(bottomView as View)


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

            sliderViewPager!!.adapter = GroupCalImageSliderAdapter(requireContext(), allImgs, uid, cid)

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



        // 0: 나만 보기, 1: 친구 공개, 2: 전체 공개
        var publicScope:Long=0

        // 놀기/방탈출
        // 놀기/연극, 뮤지컬
        var category=""
        // categories:HashMap<String, ArrayList<String>>={놀기=[방탈출, 연극, 뮤지컬], 여가=[스키], 일상=[]}

        categories_and_feedname_Callback{ s_c_hashmap->

            publicScope=s_c_hashmap["publicScope"] as Long
            category=s_c_hashmap["category"] as String

            var catidx=0
            val categoryStrList:ArrayList<String> = arrayListOf()

            for (i in 0 until (categories["CategoryList"]?.size!!)){
                categoryStrList.add("${categories["CategoryList"]?.get(i)}")
                if (("${categories["CategoryList"]?.get(i)}") == category){
                    catidx=i
                    Log.d(TAG, "catidx $catidx")
                }
            }
            Log.d(TAG, "categories $categories")
            Log.d(TAG, "categoryStrList $categoryStrList")


            spn_PostFragment_publicScope.adapter=ArrayAdapter.createFromResource(requireContext(), R.array.dialog_scope, android.R.layout.simple_spinner_item)

            spn_PostFragment_publicScope.setSelection(publicScope.toInt())

            spn_PostFragment_category.adapter=ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryStrList)

            spn_PostFragment_category.setSelection(catidx)

            spn_PostFragment_publicScope.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    publicScope=0
                    //tv_PostFragment_publicScope.text="나만 보기"
                }
                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    when (position) {
                        // 나만 보기
                        0 -> {
                            publicScope=0
                            //tv_PostFragment_publicScope.text="나만 보기"
                        }
                        // 친구 공개
                        1 -> {
                            publicScope=1
                            //tv_PostFragment_publicScope.text="친구 공개"
                        }
                        // 전체 공개
                        2 -> {
                            publicScope=2
                            //tv_PostFragment_publicScope.text="전체 공개"
                        }
                        //일치하는게 없는 경우
                        else -> {
                            publicScope=0
                            //tv_PostFragment_publicScope.text="나만 보기"

                        }
                    }
                }
            }

            spn_PostFragment_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    category="없음"
                    //tv_PostFragment_category.text=category
                }
                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    category=categoryStrList[position]
                    //tv_PostFragment_category.text=category
                }
            }

            Log.d(TAG, "publicScope $publicScope, \n category $category")

        }




        // 파베 업뎃
        writePost=view.findViewById(R.id.iv_PostFragment_writePost)

        writePost.setOnClickListener {

            var builder = AlertDialog.Builder(context)
            builder.setTitle("내용을 입력하세요")
            builder.setIcon(R.drawable.writepost)

            var viewdialog = layoutInflater.inflate(R.layout.writepost_dialog, null)
            //viewdialog.background = resources.getDrawable(R.drawable.rounded_dialog)
            builder.setView(viewdialog)


            // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
            var listener = DialogInterface.OnClickListener { p0, p1 ->
                var alert = p0 as AlertDialog
                var edit1: EditText? = alert.findViewById(R.id.et_PostFragment_dialog)

                edit1?.hint=content

                tv_PostFragment_content.text = "${edit1?.text}"
                val ncontent= edit1?.text.toString()


                lateinit var hh: HashMap<String, ArrayList<HashMap<String, Any>>>
                lateinit var contList:HashMap<String, String>
                lateinit var feedList:HashMap<String, String>


               fs.collection("calendars").whereArrayContainsAny("uidList", arrayListOf(uid)).get()
                        .addOnSuccessListener { documents->
                            for (document in documents) {
                                if (document.data["cid"].toString() == cid){
                                    hh= document.data["dataList4"] as HashMap<String, ArrayList<HashMap<String, Any>>>
                                    contList=document.data["contentList"] as HashMap<String, String>
                                    feedList=document.data["feedList"] as HashMap<String, String>
                                }
                            }

                            /**
                             * 1, 2인 것만 빼오고, 나머지는 아예 저장도 하지 말 것.
                             */
                            var finalData:ArrayList<HashMap<String, Any>> = arrayListOf()
                            val ln0:Long=0
                            for (data in galleryRVAdapter.dateGalleryData){
                                if (data["used"]==ln0){
                                    continue
                                }else{
                                    finalData.add(data)
                                }
                            }
                            Log.d(TAG, "finalData, $finalData")

                            hh[dateym]=finalData
                            contList[dateym]=ncontent


                            fs.collection("calendars").document(cid).update("dataList4", hh)
                                    .addOnSuccessListener { Log.d(TAG, "d성공") }
                                    .addOnFailureListener{ Log.d(TAG, "d실패")}
                            fs.collection("calendars").document(cid).update("contentList", contList)
                                    .addOnSuccessListener { Log.d(TAG, "c성공") }
                                    .addOnFailureListener{ Log.d(TAG, "c실패")}

                            if (feedList[dateym].equals("") || feedList[dateym]==null) {
                                // 피드 처음 생김!
                                val feedName = "Feed_" + Random().nextInt(100000)
                                val hh = hh[dateym]
                                if (hh != null) {
                                    for (data in hh) {
                                        val lnum: Long = 2
                                        if (data["used"] as Long == lnum) {
                                            // 여기서 피드에 있는 거 다 가져오는 수정 해야함!!!
                                            val feed = FeedModel(cid, uid, nickname, data["imageUri"] as String, profileImagePath, ncontent, publicScope, category, dateym, type=finalData.size)
                                            fs.collection("feeds").document(feedName).set(feed)
                                                    .addOnSuccessListener { Log.d(TAG, "f성공") }
                                                    .addOnFailureListener { Log.d(TAG, "f실패") }
                                            break
                                        }
                                    }
                                }
                                feedList[dateym] = feedName
                                fs.collection("calendars").document(cid).update("feedList", feedList)
                                        .addOnSuccessListener { Log.d(TAG, "c성공") }
                                        .addOnFailureListener { Log.d(TAG, "c실패") }
                            } else{
                                // 피드 이름 feedList에서 받아옴(수정됨)
                                val hh = hh[dateym]
                                if (hh != null) {
                                    for (data in hh) {
                                        val lnum: Long = 2
                                        if (data["used"] as Long == lnum) {
                                            val feed = FeedModel(cid, uid, nickname, data["imageUri"] as String, profileImagePath, ncontent, publicScope, category, dateym, hh.size)
                                            feedList[dateym]?.let { it1 ->
                                                fs.collection("feeds").document(it1).set(feed)
                                                        .addOnSuccessListener { Log.d(TAG, "f성공") }
                                                        .addOnFailureListener { Log.d(TAG, "f실패") }
                                            }
                                            break
                                        }

                                    }
                                }

                            }
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", null)

            builder.show()

        }


        return view
    }

    fun categories_and_feedname_Callback(callback:(HashMap<String, Any>)->Unit){
        // 카테고리 가져오기
        fs.collection("categories").document(uid).get()
                .addOnSuccessListener { documents ->
                    val categories=documents.data as HashMap<String, ArrayList<String>>
                    this.categories=categories
                    lateinit var feedListcallback:HashMap<String, String>
                    fs.collection("calendars").whereArrayContainsAny("uidList", arrayListOf(uid)).get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    if (document.data["cid"].toString() == cid) {
                                        feedListcallback = document.data["feedList"] as HashMap<String, String>
                                    }
                                }

                                try {
                                    val feedName = feedListcallback[dateym] as String
                                    fs.collection("feeds").document(feedName).get()
                                        .addOnSuccessListener {
                                            try {
                                                var hmap: HashMap<String, Any> = hashMapOf()
                                                hmap["category"] =
                                                    it.data?.get("category") as String
                                                hmap["publicScope"] =
                                                    it.data?.get("publicScope") as Long
                                                callback(hmap)
                                            }catch(e:java.lang.NullPointerException){
                                                var hmap:HashMap<String, Any> = hashMapOf()
                                                val ln0:Long=0
                                                hmap["category"]="없음"
                                                hmap["publicScope"]=ln0
                                                callback(hmap)
                                            }
                                        }
                                } catch(e:NullPointerException){
                                    var hmap:HashMap<String, Any> = hashMapOf()
                                    val ln0:Long=0
                                    hmap["category"]="없음"
                                    hmap["publicScope"]=ln0
                                    callback(hmap)
                                }

                            }
                }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "백프레스 눌름")
                val calendarFragment = GroupCalMainFragment()
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

        val TAG="갤 어댑터"



        internal fun setDataList(dateGalleryData: ArrayList<HashMap<String, Any>>) {
            this.dateGalleryData = dateGalleryData
            Log.d(TAG, "this.dateGalleryData, ${this.dateGalleryData}")
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
            storRef=storage.reference.child("groupcalendar").child(calID)
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

                sliderViewPager!!.adapter = GroupCalImageSliderAdapter(context, allImg, userID, calID)
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

                sliderViewPager!!.adapter = GroupCalImageSliderAdapter(context, allImg, userID, calID)
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