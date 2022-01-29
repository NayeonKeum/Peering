package com.awesomesol.peering.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.awesomesol.peering.calendar.CalendarFragment2

class BaseFragmentFactory(private val index: Int): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            CalendarFragment2::class.java.name -> CalendarFragment2(index)
            else -> super.instantiate(classLoader, className)
        }
    }
}