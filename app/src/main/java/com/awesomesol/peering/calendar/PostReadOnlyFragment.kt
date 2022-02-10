package com.awesomesol.peering.calendar

import com.awesomesol.peering.friend.FriendCalMainFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.awesomesol.peering.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PostReadOnlyFragment : Fragment() {
    private val TAG="갤러리"


    var fs=Firebase.firestore
    val storage=FirebaseStorage.getInstance()
    lateinit var storRef:StorageReference


    // 슬라이더
    private var sliderViewPager: ViewPager2? = null
    private var layoutIndicator: LinearLayout? = null
    private lateinit var cl_PostFragment: ConstraintLayout
    private lateinit var writePost:ImageView
    lateinit var tv_PostFragment_content:TextView
    lateinit var spn_PostFragment_publicScope:TextView
    lateinit var spn_PostFragment_category:TextView



    private var images:ArrayList<String> = arrayListOf()
    private var curDate:String=""
    private var dateym:String=""
    private var cid:String=""
    private var uid:String=""
    private var email:String=""
    private var nickname:String=""
    private var profileImagePath=""
    var group=""

    private var categories:HashMap<String, ArrayList<String>> = hashMapOf()


    private lateinit var callback:OnBackPressedCallback

    private lateinit var dateGalleryData: ArrayList<HashMap<String, Any>>
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
            email=bundle.getString("email").toString()
            content=bundle.getString("content").toString()

            uid=bundle.getString("uid").toString()
            nickname=bundle.getString("nickname").toString()
            profileImagePath=bundle.getString("profileImagePath").toString()
            group=bundle.getString("group").toString()

            Log.d(TAG, "groupY ${cid}")

            storRef = if (group == "1"){
                storage.reference.child("groupcalendar").child(cid)
            }else{
                storage.reference.child(uid).child(cid)
            }


            try{
                Log.d(TAG, "넘어온 번들: ${bundle.getSerializable("dateGalleryData")}")
                dateGalleryData = bundle.getSerializable("dateGalleryData") as ArrayList<HashMap<String, Any>>
                Log.d(TAG, "넘어온 번들 갤데화: $dateGalleryData")
            } catch(e:NullPointerException){
                dateGalleryData= arrayListOf()
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_post_read_only, container, false)

        view.findViewById<TextView>(R.id.tv_PostFragment_date).text=curDate
        tv_PostFragment_content=view.findViewById(R.id.tv_PostFragment_content)
        spn_PostFragment_publicScope=view.findViewById(R.id.spn_PostFragment_publicScope)
        spn_PostFragment_category=view.findViewById(R.id.spn_PostFragment_category)


        if (content==null){
            content=""
        }
        tv_PostFragment_content.text=content


        Log.d(TAG, "targetDate에 있는 사진 개수: " + dateGalleryData.size)
        val datasize: Int? =dateGalleryData.size
        Log.d(TAG+"뭐들었니", dateGalleryData.toString())

        val titleimgs:ArrayList<String> = arrayListOf()
        for (i : Int in 0 until datasize!!){
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

//        galleryRVAdapter.setDataList(dateGalleryData)


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

        sliderViewPager!!.adapter = ImageSliderAdapter(group, requireContext(), allImgs, uid, cid)

        sliderViewPager!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        setupIndicators(allImgs.size)


        // 0: 나만 보기, 1: 친구 공개, 2: 전체 공개
        var publicScope:Long=0

        // 놀기/방탈출
        // 놀기/연극, 뮤지컬
        var category=""
        // categories:HashMap<String, ArrayList<String>>={놀기=[방탈출, 연극, 뮤지컬], 여가=[스키], 일상=[]}

        feed_Callback{ s_c_hashmap->

            publicScope=s_c_hashmap["publicScope"] as Long
            category=s_c_hashmap["category"] as String


            val pubScopeArr= arrayListOf("나만 보기", "친구 공개", "전체 공개")
            spn_PostFragment_publicScope.text=pubScopeArr[publicScope.toInt()]
            spn_PostFragment_category.text=category
            Log.d(TAG, "publicScope $publicScope, \n category $category")

        }


        return view
    }

    // "publicScope", "category"
    fun feed_Callback(callback:(HashMap<String, Any>)->Unit){

        val ln2:Long=2
        var hmap= hashMapOf("publicScope" to ln2, "category" to "미정")
        var feedName=""

        fs.collection("calendars").document(cid).get()
            .addOnSuccessListener {
                try{
                    feedName= (it.data?.get("feedList") as HashMap<String, String>)[dateym].toString()
                    Log.d(TAG, "feedName. $feedName")
                    fs.collection("feeds").document(feedName).get()
                        .addOnSuccessListener { it1 ->
                            content= it1.data?.get("content") as String
                            hmap["category"]= it1.data!!["category"] as String
                            hmap["publicScope"]= it1.data!!["publicScope"] as Long

                            callback(hmap as HashMap<String, Any>)
                        }

                }catch(e:IllegalArgumentException){
                    callback(hmap as HashMap<String, Any>)
                }
            }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "백프레스 눌름")
                val calendarFragment = FriendCalMainFragment()
                val userBundle = Bundle()
                userBundle.putString("id", uid)
                userBundle.putString("email", email)
                userBundle.putString("nickname", nickname)
                userBundle.putString("profileImagePath", profileImagePath)
                calendarFragment.arguments = userBundle

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
