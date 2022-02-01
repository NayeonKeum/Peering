package com.awesomesol.peering.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.list_item_calendar.view.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Calendar2Adapter (val context: Context, val calendarLayout: LinearLayout, val date: Date)://, val dateGalleryData: DateGalleryData) :
    RecyclerView.Adapter<Calendar2Adapter.CalendarItemHolder>() {

    private val TAG = "캘 어댑터"
    var dataList7: ArrayList<Int> = arrayListOf()
    var dataList4: ArrayList<Int> = arrayListOf()
    val dateGalleryData:HashMap<String, ArrayList<GalleryData>> = hashMapOf("2022-01-29" to arrayListOf(GalleryData("content://media/external/images/media/100".toUri(), 1),GalleryData("content://media/external/images/media/100".toUri(), 1)),
                                                    "2022-01-22" to arrayListOf(GalleryData("content://media/external/images/media/77".toUri(), 1),GalleryData("content://media/external/images/media/78".toUri(), 1),GalleryData("content://media/external/images/media/79".toUri(), 1))
                                                    )
   // {2022-01-29=[content://media/external/images/media/100, content://media/external/images/media/101, content://media/external/images/media/102, content://media/external/images/media/103, content://media/external/images/media/104],
        // 2021-11-11=[content://media/external/images/media/32],
        // 2022-01-26=[content://media/external/images/media/82, content://media/external/images/media/83, content://media/external/images/media/84, content://media/external/images/media/92, content://media/external/images/media/85, content://media/external/images/media/86, content://media/external/images/media/91, content://media/external/images/media/87, content://media/external/images/media/88, content://media/external/images/media/90, content://media/external/images/media/89],
        // 2022-01-22=[content://media/external/images/media/77, content://media/external/images/media/78, content://media/external/images/media/79, content://media/external/images/media/80, content://media/external/images/media/81],
        // 2022-01-13=[content://media/external/images/media/75],
        // 1970-01-01=[content://media/external/images/media/26, content://media/external/images/media/27],
        // 2021-11-27=[content://media/external/images/media/53],
        // 2021-10-16=[content://media/external/images/media/23]}


    val calInstance = Calendar.getInstance()

    // FurangCalendar을 이용하여 날짜 리스트 세팅
    // var furangCalendar: MakeCalendar = MakeCalendar(date)

    init {
        calInstance.time = date
        var currentMaxDate = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..currentMaxDate){
            dataList4.add(i)
        }
        AndroidThreeTen.init(context);

        // furangCalendar.initBaseCalendar()
        //  = furangCalendar.dateList

    }



    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: CalendarItemHolder, position: Int) {

        // list_item_calendar 높이 지정
        // val h = calendarLayout.height / 6
        // holder.itemView.layoutParams.height = h

        holder?.bind(dataList4[position], position, context)
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item_calendar, parent, false)
        return CalendarItemHolder(view)
    }

    override fun getItemCount(): Int = dataList4.size

    inner class CalendarItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var itemCalendarDateText: TextView = itemView!!.item_calendar_date_text
        //var itemCalendarDotView: View = itemView!!.item_calendar_dot_view
        var iv_CalendarFragment2_img: ImageView = itemView!!.iv_CalendarFragment2_img

        @SuppressLint("ResourceAsColor")
        fun bind(data: Int, position: Int, context: Context) {
//            Log.d(TAG, "${furangCalendar.prevTail}, ${furangCalendar.nextHead}")
//            val firstDateIndex = furangCalendar.prevTail
//            val lastDateIndex = dataList2.size - furangCalendar.nextHead - 1
//            Log.d(TAG, "$firstDateIndex, $lastDateIndex")


            // 오늘 날짜 처리
            // 현재 페이지의 연월일
            var dateString: String = SimpleDateFormat("yyyy-MM", Locale.KOREA).format(date)
            // 오늘 연월일
            val formatter=org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var dateToday=LocalDate.now().format(formatter)

            dateString += if (dataList4[position]<10){
                "-0${dataList4[position]}"
            } else{
                "-${dataList4[position]}"
            }

            itemCalendarDateText.setText(data.toString())

            //var dateInt = dateString.toInt()
            if (dateToday.equals(dateString)) {
                itemCalendarDateText.setTypeface(itemCalendarDateText.typeface, Typeface.BOLD)
            }

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값의 텍스트를 회색처리
//            if (position < firstDateIndex || position > lastDateIndex) {
//                itemCalendarDateText.setTextAppearance(R.style.LightColorTextViewStyle)
//            } else{
            val date = LocalDate.parse(dateString, org.threeten.bp.format.DateTimeFormatter.ISO_DATE)
            Log.d(TAG, "dayofweek 형식: "+date.dayOfWeek)
            if (date.dayOfWeek==DayOfWeek.SATURDAY){
                itemCalendarDateText.setTextAppearance(R.style.SatTextViewStyle)
                Log.d(TAG, "${dateString}오늘은 토요일")
            }
            if (date.dayOfWeek==DayOfWeek.SUNDAY){
                itemCalendarDateText.setTextAppearance(R.style.SunTextViewStyle)
                Log.d(TAG, "${dateString}오늘은 일요일")
            }


            iv_CalendarFragment2_img.setImageResource(R.drawable.character)
            // itemView.setBackgroundColor(R.color.theme_yellow)

            try{
                iv_CalendarFragment2_img.setImageURI(dateGalleryData.get(dateString)?.get(0)?.imageUri)
            }
            finally {
                Log.d(TAG, "${dateString} 이 날 사진 없음")
            }

//            }

        }

    }
}