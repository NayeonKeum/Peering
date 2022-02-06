package com.awesomesol.peering.calendar

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_calendar2.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
    var dateGalleryData: HashMap<String, ArrayList<HashMap<String, Any>>> = hashMapOf()
    lateinit var fbtn:FloatingActionButton

    private var dataList_fromGaL: HashMap<String, ArrayList<GalleryData>> = hashMapOf()

    val fs=Firebase.firestore

    var uid:String=""
    var email:String=""
    var nickname:String=""
    var profileImagePath:String=""

    var cid:String=""

    private var pBar_CalendarFragment2: ProgressBar? = null


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
                                dateGalleryData= it.data?.get("dataList4") as HashMap<String, ArrayList<HashMap<String, Any>>>
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

        fbtn=view.findViewById(R.id.fbtn_CalendarFragment2_refreshGal)
        // dateGalleryData: HashMap<String, ArrayList<GalleryData>> = hashMapOf()
        // from fs
        // 갤러리 불러오기
        fbtn.setOnClickListener {
            setLogin(object : LoginListener {
                override fun loginClear(notices: HashMap<String, ArrayList<GalleryData>>) {
                    Log.d(TAG, "loginClear_in_cal2(dataList4)")

                    // 날짜 개수 동일
                    if (notices.size == dateGalleryData.size){
                        Toast.makeText(context, "이미 최신임", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        view.findViewById<ProgressBar>(R.id.pBar_CalendarFragment2).visibility=View.VISIBLE
                        fs.collection("users").whereEqualTo("uid", uid).get()
                                .addOnSuccessListener { documents ->

                                    // 없는 것들 넣기
                                    for (dateKey in notices.keys){
                                        if (dateGalleryData.containsKey(dateKey)){
                                            continue
                                        } else{
                                            // 키 없음
                                            val datalist:ArrayList<HashMap<String, Any>> = arrayListOf()
                                            for (data in notices[dateKey]!!){
                                                val hmap:HashMap<String, Any> =hashMapOf()
                                                hmap["imageUri"]=data.imageUri
                                                hmap["used"]=data.used
                                                datalist.add(hmap)
                                            }
                                            dateGalleryData[dateKey] = datalist
                                        }
                                    }

                                    Log.d(TAG, "캘 업뎃: $dateGalleryData")

                                    // 내 캘린더 업데이트
                                    val calData = CalendarInfo(arrayListOf(uid), cid, "내 캘린더", dateGalleryData)
                                    fs.collection("calendars").document(cid).set(calData)
                                            .addOnSuccessListener { Log.d(TAG, "캘린더 저장 성공") }
                                            .addOnFailureListener { e -> Log.d(TAG, "캘 저장 에러 났음 쨘", e) }

                                    // 새로 어댑트,,
                                    calendarAdapter = Calendar2Adapter(mContext, calendar_layout, currentDate, dateGalleryData)
                                    calendar_view.adapter = calendarAdapter
                                    calendar_view.layoutManager = GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false)
                                    calendar_view.setHasFixedSize(true)
                                    calendarAdapter.itemClick = object :
                                        Calendar2Adapter.ItemClick {
                                            override fun onClick(view: View, position: Int) {
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
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, exception.toString())
                                }
                        view.findViewById<ProgressBar>(R.id.pBar_CalendarFragment2).visibility=View.INVISIBLE
                        Toast.makeText(context, "업데이트 완료", Toast.LENGTH_SHORT).show()
                    }

                }
            })

        }

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
        calendar_year_month_text.text = datetime

    }

    fun initCalendar() {

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

    private fun setLogin(listener: LoginListener){
        var mCallback=listener
        //checkPermission() // 갤러리에 있는 data를 dataList4로 옮기는 작업스

        if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200)
        } else {
            // 갤러리 연동 분기점
            //initView()
            try {
                val cursor = getImageData()
                //getImages(cursor)
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        // 날짜별 이미지 리스트 초기화

                        //1. 각 컬럼의 열 인덱스를 취득한다.
                        val idColNum = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                        val titleColNum = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE)
                        val dateTakenColNum =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)

                        //2. 인덱스를 바탕으로 데이터를 Cursor로부터 취득하기
                        val id = cursor.getLong(idColNum) // 0
                        val title = cursor.getString(titleColNum) // 1
                        val dateTaken = cursor.getLong(dateTakenColNum) // 2
//                val imageUri =
//                        withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                        var uri= ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                        )

                        //3. 데이터를 View로 설정
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = dateTaken
                        val date = DateFormat.format("yyyy-MM-dd", calendar).toString() // "yyyy-MM-dd (E) kk:mm:ss"
                        // Log.d(TAG, date)
                        // 앞에꺼 하나만 사용, 뒤에껀 안 사용!
                        if (date in dataList_fromGaL.keys){
                            // 날짜가 이미 있다면
                            dataList_fromGaL.get(date)?.add(GalleryData(uri.toString(), 0))
                        }
                        else{
                            // 날짜가 없음!
                            dataList_fromGaL.put(date, arrayListOf())
                            dataList_fromGaL.get(date)?.add(GalleryData(uri.toString(), 1))

                        }

//                textView.text = "촬용일시: $text"
//                imageView.setImageURI(imageUri)

//                Log.d(TAG, "DATE: "+date)
//                Log.d(TAG, "ID: "+id)
//                Log.d(TAG, "TITLE: "+title)
//                Log.d(TAG, "URI: "+uri)
                    }
                    cursor.close()
                    Log.d(TAG + " 뭐 들었니", dataList_fromGaL.toString())

                }
                mCallback.loginClear(dataList_fromGaL)

            } catch (e: SecurityException) {
                Toast.makeText(context, "스토리지에 접근 권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "스토리지에 접근 권한을 허가해주세요")
                // finish()
            }
        }

        //Log.d("$TAG dataList4", dataList4.toString())

    }
    private fun getImageData(): Cursor {

        val resolver = activity?.contentResolver
        var queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        //가져올 컬럼명
        val what = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.TITLE,
                MediaStore.Images.ImageColumns.DATE_TAKEN
        )

        //정렬
        val orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " ASC"

        //1건만 가져온다.
        //queryUri = queryUri.buildUpon().appendQueryParameter("limit", "1").build()

        return resolver!!.query(queryUri, what, null, null, orderBy)!!
    }

    interface LoginListener {
        fun loginClear(notices: HashMap<String, ArrayList<GalleryData>>)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }


}