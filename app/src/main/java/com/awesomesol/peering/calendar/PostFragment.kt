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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.awesomesol.peering.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*


class PostFragment : Fragment() {


    private val TAG="갤러리"
    private var calendarImages:HashMap<String, ArrayList<String>> = hashMapOf()
    private lateinit var  galleryRVAdapter: GalleryRVAdapter
    private var targetDate="2022-01-21"
    private var dataList = mutableListOf<GalleryData>()

    private lateinit var bottomView:View
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<View>


    // 슬라이더
    private var sliderViewPager: ViewPager2? = null
    private var layoutIndicator: LinearLayout? = null

    private val images = arrayOf(
        "https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg",
        "https://cdn.pixabay.com/photo/2020/11/04/15/29/coffee-beans-5712780_1280.jpg",
        "https://cdn.pixabay.com/photo/2020/03/08/21/41/landscape-4913841_1280.jpg",
        "https://cdn.pixabay.com/photo/2020/09/02/18/03/girl-5539094_1280.jpg",
        "https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //권한 체크
        checkPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_post, container, false)

        val rv:RecyclerView=view.findViewById<View>(R.id.bottomsheetview).findViewById<RecyclerView>(
            R.id.rv_PostFragment
        )
        Log.d(TAG, rv.toString())
        //rv.layoutManager=GridLayoutManager(context, 3)
        rv.layoutManager=LinearLayoutManager(context).also{it.orientation=LinearLayoutManager.HORIZONTAL}
        galleryRVAdapter= context?.let { GalleryRVAdapter(it) }!!
        //galleryRVAdapter= context?.let { GalleryRVAdapter(it) }!!

        galleryRVAdapter.setView(view)
        //parentFragmentManager
        rv.adapter=galleryRVAdapter


        Log.d(TAG, "targetDate에 있는 사진 개수: " + calendarImages.get(targetDate)?.size)
        val datasize: Int? =calendarImages.get(targetDate)?.size

        for (i : Int in 0..(datasize!!-1)){
            calendarImages.get(targetDate)?.get(i)?.toUri()?.let { GalleryData(it, 0) }?.let {
                dataList.add(it)
            }
        }
        Log.d(TAG, dataList.toString())
        galleryRVAdapter.setDataList(dataList)


        // 바텀 쉿 부착!
        bottomView= view?.findViewById<View>(R.id.ll_PostFragment_bottomsheet)!!
        bottomSheetBehavior= BottomSheetBehavior.from(bottomView as View)

        // 전체 숨김
        // behavior.state = BottomSheetBehavior.STATE_HIDDEN
        // peekHight 만큼
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        inflater.inflate(R.layout.fragment_post_bottomsheet_photos, container).findViewById<ImageView>(
            R.id.iv_bottomsheet_up
        ).setOnClickListener {
            // 전체 보여주기
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        sliderViewPager = view.findViewById(R.id.sliderViewPager)
        layoutIndicator = view.findViewById(R.id.layoutIndicators)

        sliderViewPager!!.offscreenPageLimit = 1
        sliderViewPager!!.adapter = ImageSliderAdapter(requireContext(), images)

        sliderViewPager!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        setupIndicators(images.size)



        return view
    }

    override fun onResume() {
        super.onResume()
        LayoutInflater.from(context).inflate(R.layout.fragment_post, null, false)

    }

    // 바텀시트에 리사이클러뷰 어댑터
    class GalleryRVAdapter(var context: Context):RecyclerView.Adapter<GalleryRVAdapter.ViewHolder>() {

        var dataList = emptyList<GalleryData>()
        val TAG="갤러리 어댑터"

        lateinit var parentView: View

        lateinit private var iv_PostFragment:ImageView

        lateinit private var fragmentManager: FragmentManager

        internal fun setDataList(dataList: List<GalleryData>) {
            this.dataList = dataList
        }

        // Provide a direct reference to each of the views with data items

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var iv: ImageView
            init {
                iv = itemView.findViewById(R.id.img_PostFragment_rv_itemimg)
            }
        }

        fun setView(parentView: View){
            this.parentView=parentView
        }

        // Usually involves inflating a layout from XML and returning the holder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryRVAdapter.ViewHolder {

            // Inflate the custom layout
            var view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_post_rv_item,
                parent,
                false
            )
            iv_PostFragment=LayoutInflater.from(context).inflate(
                R.layout.fragment_post,
                null,
                false
            ).findViewById(R.id.iv_PostFragment)
            return ViewHolder(view)
        }

        // Involves populating data into the item through holder
        override fun onBindViewHolder(holder: GalleryRVAdapter.ViewHolder, position: Int) {

            // Get the data model based on position
            var data = dataList[position]

            // Set item views based on your views and data model
            holder.iv.setImageURI(data.imageUri)

            holder.iv.setOnClickListener {
                var iv_PostFragment=parentView.findViewById<ImageView>(R.id.iv_PostFragment)
                iv_PostFragment.setImageURI(data.imageUri)
            }
        }
        override fun getItemId(position: Int): Long {
            return super.getItemId(position)
        }
        //  total count of items in the list
        override fun getItemCount() = dataList.size
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


    private fun checkPermission() {
        if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200)
        } else {
            // 갤러리 연동 분기점
            initView()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            200 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView()
                } else {
                    Toast.makeText(activity, "스토리지에 접근 권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200)
                }
            }
        }
    }

    private fun initView() {
        try {
            val cursor = getImageData()

            getImages(cursor)

        } catch (e: SecurityException) {
            Toast.makeText(activity, "스토리지에 접근 권한을 허가해주세요", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "스토리지에 접근 권한을 허가해주세요")
            // finish()
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


    private fun getImages(cursor: Cursor){
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
                Log.d(TAG, date)
                if (date in calendarImages.keys){
                    // 날짜가 이미 있다면
                    calendarImages.get(date)?.add(uri.toString())
                }
                else{
                    // 날짜가 없음!
                    calendarImages.put(date, arrayListOf())
                    calendarImages.get(date)?.add(uri.toString())

                }

//                textView.text = "촬용일시: $text"
//                imageView.setImageURI(imageUri)

//                Log.d(TAG, "DATE: "+date)
//                Log.d(TAG, "ID: "+id)
//                Log.d(TAG, "TITLE: "+title)
//                Log.d(TAG, "URI: "+uri)
            }
            cursor.close()
            Log.d(TAG, calendarImages.toString())
        }
        // view?.findViewById<ImageView>(R.id.iv_CalendarFragment_test)?.setImageURI(calendarImages.get(targetDate)?.get(0)?.toUri())

    }

}