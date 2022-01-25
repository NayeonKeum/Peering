package com.awesomesol.peering.calendar

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.awesomesol.peering.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class SundayDecorator(context: Activity) : DayViewDecorator {
    private val calendar = Calendar.getInstance()
    private val context=context

    override fun shouldDecorate(day: CalendarDay): Boolean {
//        day.copyTo(calendar)
//        val weekDay = calendar[Calendar.DAY_OF_WEEK]
//        return weekDay == Calendar.SUNDAY
        return day.calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

    @SuppressLint("ResourceAsColor")
    override fun decorate(view: DayViewFacade) {
        //view.addSpan(ForegroundColorSpan(Color.RED))
        //view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)))
        view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)))
    }
}