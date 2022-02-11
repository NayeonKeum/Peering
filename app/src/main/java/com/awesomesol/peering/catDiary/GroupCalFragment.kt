package com.awesomesol.peering.catDiary

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
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.awesomesol.peering.calendar.GalleryData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_calendar2.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GroupCalFragment(index: Int, var cid: String) : Fragment() {

    private val TAG = "캘프"
    lateinit var mContext: Context
    lateinit var mActivity: MainActivity

    var pageIndex = index
    lateinit var currentDate: Date

    lateinit var calendar_year_month_text: TextView
    lateinit var calendar_layout: LinearLayout
    lateinit var calendar_view: RecyclerView
    lateinit var calendarAdapter: GroupCalAdapter
    var dateGalleryData: HashMap<String, ArrayList<HashMap<String, Any>>> = hashMapOf()
    var contentList:HashMap<String, String> = hashMapOf()
    var feedList:HashMap<String, String> = hashMapOf()

    private lateinit var dataList_fromGaL: HashMap<String, ArrayList<HashMap<String, Any>>>

    val fs=Firebase.firestore
    val storage=FirebaseStorage.getInstance()

    var uid:String=""
    var email:String=""
    var nickname:String=""
    var profileImagePath:String=""

    companion object {
        var instance: GroupCalFragment? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
            mActivity = activity as MainActivity
        }
    }

    fun galleryCallback(callback:(HashMap<String, ArrayList<HashMap<String, Any>>>)->Unit){
        var dL_fromGaL: HashMap<String, ArrayList<HashMap<String, Any>>> = hashMapOf()
        // 갤러리 데이터들 다 저장!(오직 갤러리 데이터만,,,
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

                        val ln0:Long=0
                        val ln2:Long=2
                        if (date in dL_fromGaL.keys){
                            // 날짜가 이미 있다면
                            var hmap:HashMap<String, Any> = hashMapOf()
                            hmap["imageUri"]=uri.toString()
                            hmap["used"]=ln0
                            dL_fromGaL[date]?.add(hmap)
                        }
                        else{
                            // 날짜가 없음!
                            dL_fromGaL[date]= arrayListOf()

                            var hmap:HashMap<String, Any> = hashMapOf()
                            hmap["imageUri"]=uri.toString()
                            hmap["used"]=ln2

                            dL_fromGaL[date]?.add(hmap)

                        }
                    }
                    cursor.close()
                    Log.d(TAG + " 뭐 들었니", dL_fromGaL.toString())

                }
                callback(dL_fromGaL)

            } catch (e: SecurityException) {
                Toast.makeText(context, "스토리지에 접근 권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "스토리지에 접근 권한을 허가해주세요")
            }
        }

        //Log.d("$TAG dataList4", dataList4.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this


        // 여기는 캘린더 뷰 가져오는 거(수정x)
        UserApiClient.instance.me { user, error ->
            uid = user?.id.toString()
            nickname = user?.kakaoAccount?.profile?.nickname.toString()
            profileImagePath = user?.kakaoAccount?.profile?.profileImageUrl.toString()
            email = user?.kakaoAccount?.email.toString()

            fs.collection("users").whereEqualTo("uid", uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        //Log.d(TAG, "${document.id} => ${document.data}")

                        fs.collection("calendars").document(cid).get()
                            .addOnSuccessListener {
                                dateGalleryData= it.data?.get("dataList4") as HashMap<String, ArrayList<HashMap<String, Any>>>
                                contentList=it.data?.get("contentList") as HashMap<String, String>
                                feedList=it.data?.get("feedList") as HashMap<String, String>

                                galleryCallback{dL_fromGaL->
                                    this.dataList_fromGaL=dL_fromGaL
                                    Log.d(TAG, "dataList_fromGaL, ${dataList_fromGaL}")
                                    initCalendar()
                                    // 캘린더 뷰 따로, 데이터 가져오는 거 따로
                                }

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
        calendar_year_month_text.text = datetime

    }

    fun initCalendar() {

        Log.d(TAG, "dateGalleryData $dateGalleryData")
        calendarAdapter = GroupCalAdapter(mContext, calendar_layout, currentDate, dateGalleryData, uid, cid)
        calendar_view.adapter = calendarAdapter
        calendar_view.layoutManager = GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false)
        calendar_view.setHasFixedSize(true)
        calendarAdapter.itemClick = object :
            GroupCalAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val day = calendarAdapter.datelist[position].toString()
                val date = "${calendar_year_month_text.text} ${day}일"
                var dateym: String = SimpleDateFormat("yyyy-MM", Locale.KOREA).format(currentDate.time)
                if (day.length<2){
                    dateym+= "-0$day"
                }else{
                    dateym+= "-$day"
                }

                val galleryFragment = GroupCalPostFragment()
                var bundle = Bundle()
                bundle.putString("date", date)
                bundle.putString("dateym", dateym)
                bundle.putString("cid", cid)
                bundle.putSerializable("dateGalleryData", dateGalleryData[dateym])
                bundle.putSerializable("dataList_fromGaL", dataList_fromGaL[dateym])
                // 여기에 해당날짜 내 갤러리도 넣자
                // 그럼 원래 있던 거는...? 없애.. 원래는 없는거야.. 염병.. 전부 초기화!!!!
                // 넘기고 나서 사용하면 되죠
                // dateGalleryData[dateym]이 눌이면 처음 넣는다는 거자나요?
                // 그럼 내껄 넣지요!!!

                bundle.putSerializable("feedList", feedList)
                bundle.putString("content", contentList[dateym])
                bundle.putString("uid", uid)
                bundle.putString("nickname", nickname)
                bundle.putString("profileImagePath", profileImagePath)
                galleryFragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_screen_panel, galleryFragment).commit()

            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }


}