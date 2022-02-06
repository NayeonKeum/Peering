package com.awesomesol.peering.friend

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.awesomesol.peering.calendar.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_calendar2.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FriendCalFragment(index: Int) : Fragment() {

    private val TAG = javaClass.simpleName
    lateinit var mContext: Context
    lateinit var mActivity: MainActivity

    var pageIndex = index
    lateinit var currentDate: Date

    lateinit var calendar_year_month_text: TextView
    lateinit var calendar_layout: LinearLayout
    lateinit var calendar_view: RecyclerView
    lateinit var calendarAdapter: Calendar2Adapter
    var dateGalleryData: HashMap<String, ArrayList<HashMap<String, Any>>> = hashMapOf()

    private var dataList_fromGaL: HashMap<String, ArrayList<GalleryData>> = hashMapOf()

    val fs= Firebase.firestore
    val storage= FirebaseStorage.getInstance()

    var uid:String=""
    var email:String=""
    var nickname:String=""
    var profileImagePath:String=""

    var cid:String=""

    private var pBar_CalendarFragment2: ProgressBar? = null


    companion object {
        var instance: FriendCalFragment? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
            mActivity = activity as MainActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        // 이건 정보 번들 받아야함
        uid = "2077226967"
        nickname = "예시) 금나연"
        profileImagePath = "https://k.kakaocdn.net/dn/vXU15/btrrr6F36R6/dDTklzgUtdGkHiRFZ5Mdm1/img_640x640.jpg"
        email ="ryann3@naver.com"

        fs.collection("users").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //Log.d(TAG, "${document.id} => ${document.data}")
                    val hh= document.data["calendarID"] as HashMap<String, String>
                    cid= hh["myCalendar"].toString()

                    fs.collection("calendars").document(cid).get()
                        .addOnSuccessListener {
                            dateGalleryData= it.data?.get("dataList4") as HashMap<String, ArrayList<HashMap<String, Any>>>

                            initCalendar()
                        }
                        .addOnFailureListener{
                            Log.d(TAG, "datalist4 remains null")
                            // 친구가 없으면!!!! 여기서 초대 메세지 보내면 됨!
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend_cal, container, false)
        initView(view)

        return view
    }

    fun initView(view: View) {
        pageIndex -= (Int.MAX_VALUE / 2)
        Log.e(TAG, "Calender Index: $pageIndex")
        calendar_year_month_text = view.calendar_year_month_text
        calendar_layout = view.calendar_layout
        calendar_view = view.calendar_view
        val date = Calendar.getInstance().run {
            add(Calendar.MONTH, pageIndex)
            time
        }
        currentDate = date
        Log.e(TAG, "$date")
        var datetime: String = SimpleDateFormat(
            mContext.getString(R.string.calendar_year_month_format),
            Locale.KOREA
        ).format(date.time)
        calendar_year_month_text.text = datetime

    }

    fun initCalendar() {
        Log.d(TAG, "dateGalleryData $dateGalleryData")
        calendarAdapter = Calendar2Adapter(mContext, calendar_layout, currentDate, dateGalleryData, uid, cid)
        calendar_view.adapter = calendarAdapter
        calendar_view.layoutManager =
            GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false)
        calendar_view.setHasFixedSize(true)
        calendarAdapter.itemClick = object :
            Calendar2Adapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val day = calendarAdapter.datelist[position].toString()
                val date = "${calendar_year_month_text.text} ${day}일"
                var dateym: String =
                    SimpleDateFormat("yyyy-MM", Locale.KOREA).format(currentDate.time)
                if (day.length < 2) {
                    dateym += "-0$day"
                } else {
                    dateym += "-$day"
                }

                val galleryFragment = PostFragment()
                var bundle = Bundle()
                bundle.putString("date", date)
                bundle.putString("dateym", dateym)
                bundle.putString("cid", cid)
                bundle.putSerializable("dateGalleryData", dateGalleryData[dateym])
                galleryFragment.setArguments(bundle)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_screen_panel, galleryFragment).commit()

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}