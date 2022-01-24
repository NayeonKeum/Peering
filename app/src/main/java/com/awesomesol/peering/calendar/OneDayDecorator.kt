package com.awesomesol.peering.calendar

import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class OneDayDecorator(color: Int) : DayViewDecorator {
    private var date: CalendarDay
    private val color: Int
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == date
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(StyleSpan(Typeface.BOLD))
        view.addSpan(RelativeSizeSpan(1.6f))
        view.addSpan(ForegroundColorSpan(color))
    }

    fun setDate(date: Date?) {
        this.date = CalendarDay.from(date)
    }

    init {
        date = CalendarDay.today()
        this.color = color
    }
}