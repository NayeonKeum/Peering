package com.awesomesol.peering.friend

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
import kotlin.collections.HashMap

class FriendCalAdapter(val context: Context, val calendarLayout: LinearLayout, val date: Date, val dateGalleryData: HashMap<String, ArrayList<HashMap<String, Any>>>) :
    RecyclerView.Adapter<FriendCalAdapter.CalendarItemHolder>() {

    private val TAG = "캘 어댑터"
    var datelist: ArrayList<Int> = arrayListOf()

    val calInstance = Calendar.getInstance()

    init {
        calInstance.time = date
        var currentMaxDate = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..currentMaxDate){
            datelist.add(i)
        }
        AndroidThreeTen.init(context);

    }


    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: CalendarItemHolder, position: Int) {

        holder?.bind(datelist[position], position, context)
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

    override fun getItemCount(): Int = datelist.size

    inner class CalendarItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var itemCalendarDateText: TextView = itemView!!.item_calendar_date_text
        // var itemCalendarDotView: View = itemView!!.item_calendar_dot_view
        var iv_CalendarFragment2_img: ImageView = itemView!!.iv_CalendarFragment2_img
        var iv_CalendarFragment2_character: ImageView =itemView!!.iv_CalendarFragment2_character

        @SuppressLint("ResourceAsColor")
        fun bind(data: Int, position: Int, context: Context) {

            // 오늘 날짜 처리
            // 현재 페이지의 연월일
            var dateString: String = SimpleDateFormat("yyyy-MM", Locale.KOREA).format(date)
            // 오늘 연월일
            val formatter=org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var dateToday= LocalDate.now().format(formatter)

            dateString += if (datelist[position]<10){
                "-0${datelist[position]}"
            } else{
                "-${datelist[position]}"
            }

            itemCalendarDateText.setText(data.toString())


            val date = LocalDate.parse(dateString, org.threeten.bp.format.DateTimeFormatter.ISO_DATE)
            if (date.dayOfWeek== DayOfWeek.SATURDAY){
                itemCalendarDateText.setTextAppearance(R.style.SatTextViewStyle)
            }
            if (date.dayOfWeek== DayOfWeek.SUNDAY){
                itemCalendarDateText.setTextAppearance(R.style.SunTextViewStyle)
            }
            //var dateInt = dateString.toInt()
            if (dateToday.equals(dateString)) {
                itemCalendarDateText.setTypeface(itemCalendarDateText.typeface, Typeface.BOLD)
            }


            val rand= Random().nextInt(100)
            if (rand%2==0){
                iv_CalendarFragment2_character.setImageResource(R.drawable.character_blue)
            }

            try{
                val hh= dateGalleryData[dateString] as ArrayList<HashMap<String, Any>>
                if (hh != null) {
                    for (data in hh){
                        val lnum:Long=1
                        if (data["used"]!!.equals(lnum)){
                            val uri=data["imageUri"] as String
                            iv_CalendarFragment2_img.setImageURI(uri.toUri())
                            itemView.findViewById<ConstraintLayout>(R.id.cl_CalendarFragment2).setBackgroundColor(
                                Color.parseColor("#FFB6B9"))
                            iv_CalendarFragment2_character.visibility= View.GONE
                            break
                        }
                        // 깃헙 뭔가 이상해 그러지마 제발
                        // 전부 다 0이면! 여기서 지정
                        /*
                                * 제일 처음걸로 기본 지정
                                * 배경 색 변경
                                * */
                    }
                }else{
                    Log.d(TAG, "data가 눌이여")
                }

            }
            catch(e :NullPointerException){
                Log.d(TAG, "${dateString} 이 날 사진 없음")

            }
            finally {

            }


        }

    }

}