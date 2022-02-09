package com.awesomesol.peering.catDiary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.awesomesol.peering.calendar.PostFragment
import com.awesomesol.peering.common.BaseFragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_first.view.*
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GroupCalMainFragment : BaseFragment() {
    
    val TAG="그룹캘"

    lateinit var id:String
    lateinit var email:String
    lateinit var nickname:String
    lateinit var profileImagePath: String
    lateinit var followingNum: String
    lateinit var followerNum:String
    lateinit var progressNow: String
    lateinit var dDay:String
    lateinit var diaryCnt:String
    lateinit var diaryLeftCnt: String
    lateinit var gid:String
    lateinit var cid:String
    lateinit var uidList:ArrayList<String>


    lateinit var mContext: Context
    lateinit var calendarViewPager: ViewPager2
    private lateinit var iv_CalendarFragment2_leftarr:ImageView
    private lateinit var iv_CalendarFragment2_righttarr:ImageView

    private var users:ArrayList<HashMap<String, Any>> = arrayListOf()

    private lateinit var  userRVAdapter: UserRVAdapter


    val fs= Firebase.firestore
    val storage= FirebaseStorage.getInstance()




    companion object {
        var instance: GroupCalMainFragment? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            id = it.getString("id").toString()
//            email = it.getString("email").toString()
//            nickname = it.getString("nickname").toString()
//            profileImagePath = it.getString("profileImagePath").toString()
            // 이건 정보 번들 받아야함
            id = "2077226967"
            cid="calendar573471"
            nickname = "예시) 금나연"
            profileImagePath = "https://k.kakaocdn.net/dn/vXU15/btrrr6F36R6/dDTklzgUtdGkHiRFZ5Mdm1/img_640x640.jpg"
            email ="ryann3@naver.com"
            followingNum = "140"
            gid="grouptest"

        }

        instance = this


    }

    fun groupNuserCallback(callback:(String)->Unit){
        fs.collection("groups").document(gid).get()
            .addOnSuccessListener {
              Log.d(TAG, "it.data, ${it.data}")
              // callback(it.data as HashMap<String, Any>)
            nickname= it.data?.get("groupName") as String
            followingNum= it.data!!["groupNum"] as String
            cid= it.data!!["cid"] as String
            uidList= it.data!!["uidList"] as ArrayList<String>
            profileImagePath=it.data!!["groupImg"] as String

            Log.d(TAG, "cid $cid")
            fs.collection("users").get()
                .addOnSuccessListener { documents->
                    for (data in documents){
                        users.add(data.data as HashMap<String, Any>)
                    }
                    Log.d(TAG, "users, $users")
                    callback("성공")
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val viewFrag = inflater.inflate(R.layout.fragment_group_cal_main, container, false)
        calendarViewPager = viewFrag.calendarViewPager

        groupNuserCallback {
            viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_nickname)?.text = nickname

            viewFrag?.findViewById<ImageView>(R.id.iv_CharacterFragment_profileImg)?.let {
                Glide.with(viewFrag)
                    .load(profileImagePath)
                    .circleCrop()
                    .into(it)
            }
            viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_followingNum)?.text =
                followingNum

            // 리사이클러뷰 어댑트
            // uid:[cid:slkdf, gr:sdkfjs]
            var rvData: ArrayList<HashMap<String, Any>> = arrayListOf()
            for (uid in uidList) {
                for (user in users) {
                    if (user["uid"] == uid) {
                        rvData.add(user)
                    }
                }
            }

            val rv: RecyclerView = viewFrag.findViewById(R.id.rv_PostFragment)
            rv.layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
            userRVAdapter = context?.let { UserRVAdapter(it, cid) }!!
            userRVAdapter.setView(viewFrag)

            //parentFragmentManager
            rv.adapter = userRVAdapter
            userRVAdapter.setDataList(rvData)

        }

        viewFrag?.findViewById<Button>(R.id.btn_CalendarFragment_writePost)
            ?.setOnClickListener {
                val galleryFragment = PostFragment()
                val formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                var date = LocalDate.now().format(formatter)
                var bundle = Bundle()
                bundle.putString("date", date)
                galleryFragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_screen_panel, galleryFragment).commit()
            }

        iv_CalendarFragment2_leftarr=viewFrag.findViewById(R.id.iv_CalendarFragment2_leftarr)
        iv_CalendarFragment2_righttarr=viewFrag.findViewById(R.id.iv_CalendarFragment2_rightarr)
        
        return viewFrag
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        val calendarPagerFragmentStateAdapter = GroupCalPagerFragmentStateAdapter(requireActivity())
        calendarViewPager.adapter = calendarPagerFragmentStateAdapter
        calendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        calendarPagerFragmentStateAdapter.apply {
            calendarViewPager.setCurrentItem(this.firstFragmentPosition, false)
        }

        iv_CalendarFragment2_leftarr.setOnClickListener {
            var current = calendarViewPager.currentItem
            Log.d(TAG, "왼")
            calendarViewPager.setCurrentItem(current-1, false)

        }
        iv_CalendarFragment2_righttarr.setOnClickListener {
            var current = calendarViewPager.currentItem
            Log.d(TAG, "오")
            calendarViewPager.setCurrentItem(current+1, false)
        }


    }

    // 바텀시트에 리사이클러뷰 어댑터
    class UserRVAdapter(var context: Context, var cid:String): RecyclerView.Adapter<UserRVAdapter.ViewHolder>() {


        var rvData = ArrayList<HashMap<String, Any>>()
        lateinit var parentView: View


        internal fun setDataList(rvData: ArrayList<HashMap<String, Any>>) {
            this.rvData = rvData
        }

        // Provide a direct reference to each of the views with data items
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var iv: ImageView

            init {
                iv = itemView.findViewById(R.id.iv_GroupCalFragment_userImg)
            }
        }

        fun setView(parentView: View){
            this.parentView = parentView
        }

        // Usually involves inflating a layout from XML and returning the holder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRVAdapter.ViewHolder {
            // Inflate the custom layout
            var view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_group_cal_rv_item,
                parent,
                false
            )

            return ViewHolder(view)
        }

        // Involves populating data into the item through holder
        override fun onBindViewHolder(holder: UserRVAdapter.ViewHolder, position: Int) {

            var user = rvData[position]

            Glide.with(context)
                .load(user["profileUrl"].toString())
                .circleCrop()
                .into(holder.iv)

            holder.iv.setOnClickListener {
                // 미니 정보
                // 일단 사진만
            }
        }
        override fun getItemId(position: Int): Long {
            return super.getItemId(position)
        }
        //  total count of items in the list
        override fun getItemCount() = rvData.size

    }


    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}