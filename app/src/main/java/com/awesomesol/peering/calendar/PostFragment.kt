package com.awesomesol.peering.calendar

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class PostFragment : Fragment() {


    private val TAG="갤러리"
    private var calendarImages:HashMap<String, ArrayList<String>> = hashMapOf()
    private lateinit var  galleryRVAdapter: GalleryRVAdapter
    private var targetDate="2022-01-29"
    private var dataList = mutableListOf<GalleryData>()

    private lateinit var bottomView:View
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<View>


    // 슬라이더
    private var sliderViewPager: ViewPager2? = null
    private var layoutIndicator: LinearLayout? = null
    private lateinit var cl_PostFragment: ConstraintLayout

    private var images:ArrayList<String> = arrayListOf()

    private lateinit var callback:OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //권한 체크
        checkPermission()

        var bundle = arguments;  //번들 받기. getArguments() 메소드로 받음.
        if (bundle != null){
            // 눌렀을 때 제대로 들어오면!
            // targetDate = bundle.getString("targetDate").toString(); //Name 받기.
            Log.d(TAG, "넘어온 번들: ${bundle.getString("date")}")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_post, container, false)

        // view.findViewById<TextView>(R.id.tv_PostFragment_date).text=targetDate


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
        Log.d(TAG+"뭐들었니", calendarImages.toString())

        for (i : Int in 0..(datasize!!-1)){
            calendarImages.get(targetDate)?.get(i)?.toUri()?.let { GalleryData(it, 0) }?.let {
                dataList.add(it)
                // 초기엔 뭐가 없으니까 일단!!
                images.add(it.imageUri.toString())
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

        // 둥근 모서리
        cl_PostFragment=view.findViewById(R.id.cl_PostFragment)
        cl_PostFragment.clipToOutline=true

        // 초기에 해주는 거!
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
        sliderViewPager!!.refreshDrawableState()

    }

    // 바텀시트에 리사이클러뷰 어댑터
    class GalleryRVAdapter(var context: Context):RecyclerView.Adapter<GalleryRVAdapter.ViewHolder>() {

        var dataList = emptyList<GalleryData>()
        var useImages:ArrayList<String> = arrayListOf()
        //val TAG="갤러리 어댑터"

        lateinit var parentView: View

        lateinit var sliderViewPager:ViewPager2
        lateinit var cl_PostFragment:ConstraintLayout
        lateinit var layoutIndicator:LinearLayout

        internal fun setDataList(dataList: List<GalleryData>) {
            this.dataList = dataList
        }

        // Provide a direct reference to each of the views with data items
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var iv: ImageView
            var addminus:ImageView
            init {
                iv = itemView.findViewById(R.id.img_PostFragment_rv_itemimg)
                addminus=itemView.findViewById(R.id.iv_PostFragment_addminusbtn)
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
            var data = dataList[position]
            if(dataList[position].used==0){
                holder.addminus.setImageResource(R.drawable.gallery_add)
            }
            else{
                holder.addminus.setImageResource(R.drawable.gallery_minus)
                holder.addminus.setImageResource(R.drawable.gallery_minus)
            }

            // Set item views based on your views and data model
            holder.iv.setImageURI(data.imageUri)

            // 눌렀을 때 전달을 그 어댑터에 전달을 해야하네,, 이미지 어댑터..!!
            holder.iv.setOnClickListener {
                Log.d("뷰페이저", dataList[position].imageUri.toString())
                if(dataList[position].used==0){
                    // 사용 안 한 거
                        // 더하는 액션 하고
                    dataList[position].used=1 // 이거 서버에 전달해야햠
                    holder.addminus.setImageResource(R.drawable.gallery_minus)
                }
                else{
                    // 사용 한 거
                        // 빼는 액션하고
                    dataList[position].used=0 // 이거 서버에 전달해야햠
                    holder.addminus.setImageResource(R.drawable.gallery_add)
                }

                useImages= arrayListOf()
                for (i:Int in dataList.indices){
                    if(dataList[i].used==1){
                        useImages.add(dataList[i].imageUri.toString())
                    }
                }
                layoutIndicator!!.removeAllViews()

                // 둥근 모서리
                cl_PostFragment.clipToOutline=true

                sliderViewPager!!.adapter = ImageSliderAdapter(context, useImages)
                sliderViewPager!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentIndicator(position)
                    }
                })
                setupIndicators(useImages.size)

            }
        }
        override fun getItemId(position: Int): Long {
            return super.getItemId(position)
        }
        //  total count of items in the list
        override fun getItemCount() = dataList.size


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

    override fun onDetach(){
        super.onDetach()
        callback.remove()
    }
}