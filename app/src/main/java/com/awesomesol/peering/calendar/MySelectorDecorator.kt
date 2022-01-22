package com.awesomesol.peering.calendar

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.awesomesol.peering.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class MySelectorDecorator(context: Activity) : DayViewDecorator {
    private val drawable = context.resources.getDrawable(R.drawable.calendar_my_selector)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return true
    }

    @SuppressLint("ResourceAsColor")
    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(R.color.black))
        view.addSpan(StyleSpan(Typeface.BOLD))
        view.addSpan(RelativeSizeSpan(1.2f))
        view.setSelectionDrawable(drawable)
    }

}