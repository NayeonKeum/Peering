package com.awesomesol.peering.calendar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.activity_splash.view.*
import java.util.*
import java.util.concurrent.Executors

class CalendarFragment : Fragment() {


    private lateinit var calendarView: MaterialCalendarView

    private lateinit var result: Array<String>
    private lateinit var result_ratio: DoubleArray
    
    val TAG="캘린더"


    var date_rate: HashMap<String, Double> = hashMapOf(
        Pair("2022-01-01", 0.1), Pair("2022-01-02", 0.2), Pair("2022-01-05", 0.0), Pair("2022-01-07", 0.1), Pair("2022-01-22", 0.0)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 글쓰기로!
        view?.findViewById<Button>(R.id.btn_CalendarFragment_writePost)?.setOnClickListener {
            val galleryFragment = GalleryFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, galleryFragment).commit()
        }

        calendarView = view?.findViewById(R.id.calendarView)!!

        calendarView.addDecorators(activity?.let { MySelectorDecorator(it) }, activity?.let { SundayDecorator(it) }, SaturdayDecorator())

        // 오늘 날짜 색
        val oneDayDecorator = OneDayDecorator(context?.resources!!.getColor(R.color.black))
        calendarView.addDecorator(oneDayDecorator)
        
        calendarView.selectedDate = CalendarDay.today()
        //calendarView.addDecorator()




        result = date_rate.keys.toTypedArray()
        result_ratio = DoubleArray(result.size)
        val ratio_col: Collection<Double> = date_rate.values
        for (i in ratio_col.indices) {
            if (ratio_col.toTypedArray()[i].javaClass.name == "java.lang.Long") {
                if (ratio_col.toTypedArray()[i].toLong() == 0L) {
                    result_ratio[i] = 0.0
                } else if (ratio_col.toTypedArray()[i].toLong() == 1L) {
                    result_ratio[i] = 1.0
                }
            } else {
                result_ratio[i] = ratio_col.toTypedArray()[i]
            }
        }

        ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

//        calendarView.setOnDateChangedListener { widget, date, selected ->
//            val Year: Int = date.year
//            val Month: Int = date.month + 1
//            val Day: Int = date.day
//            var selectedDate: String? = null
//
//        }
    }


    inner class ApiSimulator(var Time_Result: Array<String>) : AsyncTask<Void?, Void?, List<CalendarDay>>() {
        override fun doInBackground(vararg p0: Void?): List<CalendarDay>{
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            // string 문자열인 Time_Result 을 받아와서 ,를 기준으로 자르고 string을 int 로 변환
            val calendar = Calendar.getInstance()
            val dates: ArrayList<CalendarDay> = ArrayList<CalendarDay>()
            for (i in 0 until Time_Result.size + 1) {
                val day: CalendarDay = CalendarDay.from(calendar)
                if (i >= 0 && i < Time_Result.size) {
                    val time = Time_Result[i]!!.split("-").toTypedArray()
                    val year = time[0].toInt()
                    val month = time[1].toInt()
                    val dayy = time[2].toInt()
                    calendar[year, month - 1] = dayy
                }
                if (i > 0) {
                    dates.add(day)
                }
            }
            for (i in dates.indices) {
                Log.d(TAG, "고른 날짜들: "+dates[i].toString())
            }
            return dates
        }

        override fun onPostExecute(calendarDays: List<CalendarDay>) {
            super.onPostExecute(calendarDays)
            calendarView.addDecorators(
                activity?.let { EventDecorator0_4(calendarDays, result_ratio, it) },
            )}

    }

}