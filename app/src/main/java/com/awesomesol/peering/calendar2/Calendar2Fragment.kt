package com.awesomesol.peering.calendar2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.awesomesol.peering.R
import java.util.*
import kotlin.collections.ArrayList


class Calendar2Fragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_calendar2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val events: MutableList<EventDay> = ArrayList()

        val calendar: Calendar = Calendar.getInstance()
        //events.add(EventDay(calendar, R.drawable.bottom_home))
        //or
        //events.add(EventDay(calendar, Drawable()))
        //or if you want to specify event label color
        events.add(
            EventDay(
                calendar,
                R.drawable.bottom_home,
                context?.resources!!.getColor(R.color.theme_red)
            )
        )

        val calendarView: CalendarView = view?.findViewById(R.id.calendarView) as CalendarView
        calendarView.setEvents(events)
        // CalendarUtils.getDrawableText(context, String text, Typeface typeface, int color, int size);
        calendarView.setOnDayClickListener { eventDay ->
            val clickedDayCalendar = eventDay.calendar
        }

        val selectedDates = calendarView.selectedDates
    }


}