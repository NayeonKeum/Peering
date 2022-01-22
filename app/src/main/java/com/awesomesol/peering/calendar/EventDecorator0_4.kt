package com.awesomesol.peering.calendar

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.Log
import com.awesomesol.peering.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

/**
 * Decorate several days with a dot
 */
class EventDecorator0_4(
    color: Int,
    dates: Collection<CalendarDay?>,
    result_ratio: DoubleArray,
    context: Activity
) : DayViewDecorator {
    private val drawable: Drawable = context.resources.getDrawable(R.drawable.more0_4)
    private val color: Int = color
    private val dates = HashSet<CalendarDay>()
    private val result_ratio: DoubleArray = result_ratio
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable)
    }

    //{0.0,0.2,0.3,0.5, 0.6,0.78,1.0};
    init {
        //this.dates = new HashSet<>(dates);
        for (i in dates.indices) {
            if (result_ratio[i] > -1) { //==0.0){
                Log.d(
                    "필터 성공 ",
                    dates.toTypedArray()[i].toString() + " " + result_ratio[i] + " " + i
                )
                this.dates.add(dates.toTypedArray()[i] as CalendarDay)
            }
        }
    }
}