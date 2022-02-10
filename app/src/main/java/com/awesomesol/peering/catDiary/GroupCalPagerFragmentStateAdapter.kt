package com.awesomesol.peering.catDiary

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class GroupCalPagerFragmentStateAdapter(fragmentActivity: FragmentActivity, val cid:String): FragmentStateAdapter(fragmentActivity) {

    private val pageCount = Int.MAX_VALUE
    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        val calendarFragment = GroupCalFragment(position, cid)
//        calendarFragment.pageIndex = position
        return calendarFragment
    }

}
