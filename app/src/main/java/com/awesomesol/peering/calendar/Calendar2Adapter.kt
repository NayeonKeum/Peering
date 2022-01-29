package com.awesomesol.peering.calendar

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import kotlinx.android.synthetic.main.list_item_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Calendar2Adapter (val context: Context, val calendarLayout: LinearLayout, val date: Date) :
    RecyclerView.Adapter<Calendar2Adapter.CalendarItemHolder>() {

    private val TAG = "캘 어댑터"
    var dataList: ArrayList<Int> = arrayListOf()

    // FurangCalendar을 이용하여 날짜 리스트 세팅
    var furangCalendar: MakeCalendar = MakeCalendar(date)
    init {
        furangCalendar.initBaseCalendar()
        dataList = furangCalendar.dateList
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: CalendarItemHolder, position: Int) {

        // list_item_calendar 높이 지정
        val h = calendarLayout.height / 6
        holder.itemView.layoutParams.height = h

        holder?.bind(dataList[position], position, context)
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

    override fun getItemCount(): Int = dataList.size

    inner class CalendarItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var itemCalendarDateText: TextView = itemView!!.item_calendar_date_text
        var itemCalendarDotView: View = itemView!!.item_calendar_dot_view

        fun bind(data: Int, position: Int, context: Context) {
//            Log.d(TAG, "${furangCalendar.prevTail}, ${furangCalendar.nextHead}")
            val firstDateIndex = furangCalendar.prevTail
            val lastDateIndex = dataList.size - furangCalendar.nextHead - 1
//            Log.d(TAG, "$firstDateIndex, $lastDateIndex")
//
//            if (dataList[position] == 1) {
//                itemCalendarDotView.setBackgroundResource(R.drawable.shape_circle_routine_red)
//            }
//            if (dataList[position] == 2) {
//                itemCalendarDotView.setBackgroundResource(R.drawable.shape_circle_routine_orange)
//            }
//            if (dataList[position] == 3) {
//                itemCalendarDotView.setBackgroundResource(R.drawable.shape_circle_routine_yellow)
//            }
//            if (dataList[position] == 4) {
//                itemCalendarDotView.setBackgroundResource(R.drawable.shape_circle_routine_green)
//            }
//            if (dataList[position] == 5) {
//                itemCalendarDotView.setBackgroundResource(R.drawable.shape_circle_routine_blue)
//            }
//            if (dataList[position] == 6) {
//                itemCalendarDotView.setBackgroundResource(R.drawable.shape_circle_routine_navy)
//            }
//            if (dataList[position] == 7) {
//                itemCalendarDotView.setBackgroundResource(R.drawable.shape_circle_routine_purple)
//            }
//
            itemCalendarDateText.setText(data.toString())

            // 오늘 날짜 처리
            var dateString: String = SimpleDateFormat("dd", Locale.KOREA).format(date)
            var dateInt = dateString.toInt()
            if (dataList[position] == dateInt) {
                itemCalendarDateText.setTypeface(itemCalendarDateText.typeface, Typeface.BOLD)
            }

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값의 텍스트를 회색처리
            if (position < firstDateIndex || position > lastDateIndex) {
                itemCalendarDateText.setTextAppearance(R.style.LightColorTextViewStyle)
                itemCalendarDotView.background = null
//                itemCalendarDotView.setBackgroundResource(R.drawable.shape_circle_routine_light_gray)
            }
        }

    }
}