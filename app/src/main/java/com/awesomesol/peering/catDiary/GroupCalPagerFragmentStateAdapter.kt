package com.awesomesol.peering.catDiary

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class GroupCalPagerFragmentStateAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val pageCount = Int.MAX_VALUE
    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        val calendarFragment = GroupCalFragment(position)
//        calendarFragment.pageIndex = position
        return calendarFragment
    }
//    override fun createFragment(position: Int): Fragment =
//        CalendarFragment(position)


//    var fragments = mutableListOf<CalendarFragment>()
//    val firstFragmentPosition = Int.MAX_VALUE / 2

//    override fun getItemCount(): Int = if (fragments.isNotEmpty()) Int.MAX_VALUE else 0
//
//    override fun createFragment(position: Int): Fragment =
//        CalendarFragment(fragments[position.rem(fragments.size)], position)
//
//    fun updateFragments(list: List<CalendarFragment>) {
//        fragments.apply {
//            clear()
//            addAll(list)
//        }
//        notifyDataSetChanged()
//    }
}
