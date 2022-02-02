package com.awesomesol.peering.calendar

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_calendar2.view.*
import java.text.SimpleDateFormat
import java.util.*
class CalendarFragment2(index: Int) : Fragment() {

    private val TAG = "캘프"
    lateinit var mContext: Context
    lateinit var mActivity: MainActivity

    var pageIndex = index
    lateinit var currentDate: Date

    lateinit var calendar_year_month_text: TextView
    lateinit var calendar_layout: LinearLayout
    lateinit var calendar_view: RecyclerView
    lateinit var calendarAdapter: Calendar2Adapter
    var dateGalleryData: HashMap<String, ArrayList<GalleryData>> = hashMapOf()

    val fs=Firebase.firestore

    var uid:String=""
    var email:String=""
    var nickname:String=""
    var profileImagePath:String=""

    var cid:String=""


    companion object {
        var instance: CalendarFragment2? = null
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


        UserApiClient.instance.me { user, error ->
            uid = user?.id.toString()
            nickname = user?.kakaoAccount?.profile?.nickname.toString()
            profileImagePath = user?.kakaoAccount?.profile?.profileImageUrl.toString()
            email = user?.kakaoAccount?.email.toString()

            fs.collection("users").whereEqualTo("uid", uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        //Log.d(TAG, "${document.id} => ${document.data}")
                        val hh= document.data["calendarID"] as HashMap<String, String>
                        cid= hh["myCalendar"].toString()

                        fs.collection("calendars").document(cid).get()
                            .addOnSuccessListener {
                                dateGalleryData= it.data?.get("dataList4") as HashMap<String, ArrayList<GalleryData>>
                                // Log.d(TAG, "dateList4 $dateGalleryData")
                                initCalendar()
                            }
                            .addOnFailureListener{
                                Log.d(TAG, "datalist4 remains null")
                            }

                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar2, container, false)
        initView(view)
        //initCalendar()


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
        calendar_year_month_text.setText(datetime)

    }

    fun initCalendar() {
        // 각 월의 1일의 요일을 구해
        // 첫 주의 일요일~해당 요일 전까지는 ""으로
        // 말일까지 해당 날짜
        // 마지막 날짜 뒤로는 ""으로 처리하여
        // CalendarAdapter로 List를 넘김
        Log.d(TAG, "dateGalleryData $dateGalleryData")
        calendarAdapter = Calendar2Adapter(mContext, calendar_layout, currentDate, dateGalleryData)
        calendar_view.adapter = calendarAdapter
        calendar_view.layoutManager = GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false)
        calendar_view.setHasFixedSize(true)
        calendarAdapter.itemClick = object :
            Calendar2Adapter.ItemClick {
            override fun onClick(view: View, position: Int) {
//                val firstDateIndex = calendarAdapter.dataList.indexOf(1)
//                val lastDateIndex = calendarAdapter.dataList.lastIndexOf(calendarAdapter.furangCalendar.currentMaxDate)
//                // 현재 월의 1일 이전, 현재 월의 마지막일 이후는 터치 disable
//                if (position < firstDateIndex || position > lastDateIndex) {
//                    return
//                }
                val day = calendarAdapter.datelist[position].toString()
                val date = "${calendar_year_month_text.text} ${day}일"
                var dateym: String = SimpleDateFormat("yyyy-MM", Locale.KOREA).format(currentDate.time)
                if (day.length<2){
                    dateym+= "-0$day"
                }else{
                    dateym+= "-$day"
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