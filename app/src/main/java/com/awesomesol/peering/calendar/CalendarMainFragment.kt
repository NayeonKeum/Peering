package com.awesomesol.peering.calendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.awesomesol.peering.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_first.view.*
import org.threeten.bp.LocalDate

class CalendarMainFragment : BaseFragment() {

    private val TAG = "캘린더 메인 프랙"
    lateinit var mContext: Context

    lateinit var calendarViewPager: ViewPager2
    private lateinit var iv_CalendarFragment2_leftarr:ImageView
    private lateinit var iv_CalendarFragment2_righttarr:ImageView

    companion object {
        var instance: CalendarMainFragment? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        calendarViewPager = view.calendarViewPager


        view?.findViewById<Button>(R.id.btn_CalendarFragment_writePost)?.setOnClickListener {
            val galleryFragment = PostFragment()
            val formatter=org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var date= LocalDate.now().format(formatter)
            var bundle = Bundle()
            bundle.putString("date", date)
            galleryFragment.setArguments(bundle)
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, galleryFragment).commit()
        }

        iv_CalendarFragment2_leftarr=view.findViewById(R.id.iv_CalendarFragment2_leftarr)
        iv_CalendarFragment2_righttarr=view.findViewById(R.id.iv_CalendarFragment2_rightarr)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        val calendarPagerFragmentStateAdapter = CalendarPagerFragmentStateAdapter(requireActivity())
        calendarViewPager.adapter = calendarPagerFragmentStateAdapter
        calendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        calendarPagerFragmentStateAdapter.apply {
            calendarViewPager.setCurrentItem(this.firstFragmentPosition, false)
        }


        iv_CalendarFragment2_leftarr.setOnClickListener {
            var current = calendarViewPager.currentItem
            calendarViewPager.setCurrentItem(current-1, false)

        }
        iv_CalendarFragment2_righttarr.setOnClickListener {
            var current = calendarViewPager.currentItem
            calendarViewPager.setCurrentItem(current+1, false)
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}