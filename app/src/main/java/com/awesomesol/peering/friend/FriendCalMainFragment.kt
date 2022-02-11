package com.awesomesol.peering.friend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.MainActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_first.view.*

class FriendCalMainFragment : Fragment() {
    lateinit var id:String
    lateinit var email:String
    lateinit var nickname:String
    lateinit var profileImagePath: String
    lateinit var followingNum: String
    lateinit var followerNum:String
    lateinit var progressNow: String
    lateinit var dDay:String
    lateinit var diaryCnt:String
    lateinit var diaryLeftCnt: String


    private val TAG=javaClass.simpleName

    lateinit var mContext: Context
    lateinit var calendarViewPager: ViewPager2
    private lateinit var iv_CalendarFragment2_leftarr:ImageView
    private lateinit var iv_CalendarFragment2_righttarr:ImageView

    companion object {
        var instance: FriendCalMainFragment? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
//            id = it.getString("id").toString()
//            email = it.getString("email").toString()
//            nickname = it.getString("nickname").toString()
//            profileImagePath = it.getString("profileImagePath").toString()
            // 이건 정보 번들 받아야함
            id = "2077226967"
            nickname = "금나연"
            profileImagePath = "https://k.kakaocdn.net/dn/vXU15/btrrr6F36R6/dDTklzgUtdGkHiRFZ5Mdm1/img_640x640.jpg"
            email ="ryann3@naver.com"
            //나중에 데이터 제대로 연결해야함
            progressNow = "70"
            followingNum = "140"
            followerNum = "200"
            dDay = "100"
            diaryCnt="10"
            diaryLeftCnt="2"
        }

        id = "2077226967"
        nickname = "금나연"
        profileImagePath = "https://k.kakaocdn.net/dn/vXU15/btrrr6F36R6/dDTklzgUtdGkHiRFZ5Mdm1/img_640x640.jpg"
        email ="ryann3@naver.com"
        //나중에 데이터 제대로 연결해야함
        progressNow = "70"
        followingNum = "140"
        followerNum = "200"
        dDay = "100"
        diaryCnt="10"
        diaryLeftCnt="2"

        instance = this

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var viewFrag=inflater.inflate(R.layout.fragment_friend_cal_main, container, false)
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_nickname)?.text=nickname
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_email)?.text=email

        viewFrag?.findViewById<ImageView>(R.id.iv_CharacterFragment_profileImg)?.let {
            Glide.with(viewFrag)
                    .load(profileImagePath)
                    .circleCrop()
                    .into(it)
        }

        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_followingNum)?.text=followingNum
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_followerNum)?.text=followerNum
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_dDay)?.text=dDay
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_diaryCnt)?.text=diaryCnt
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_diaryLeftCnt)?.text=diaryLeftCnt
        viewFrag?.findViewById<ProgressBar>(R.id.progressBar_CharacterFragment)?.progress=progressNow.toInt()




        calendarViewPager = viewFrag.calendarViewPager
        iv_CalendarFragment2_leftarr=viewFrag.findViewById(R.id.iv_CalendarFragment2_leftarr)
        iv_CalendarFragment2_righttarr=viewFrag.findViewById(R.id.iv_CalendarFragment2_rightarr)


        return viewFrag
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        val friendcalPagerFragmentStateAdapter = FriendCalPagerFragmentStateAdapter(requireActivity())
        calendarViewPager.adapter = friendcalPagerFragmentStateAdapter
        calendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        friendcalPagerFragmentStateAdapter.apply {
            calendarViewPager.setCurrentItem(this.firstFragmentPosition, false)
        }

        iv_CalendarFragment2_leftarr.setOnClickListener {
            var current = calendarViewPager.currentItem
            Log.d(TAG, "왼")
            calendarViewPager.setCurrentItem(current-1, false)

        }
        iv_CalendarFragment2_righttarr.setOnClickListener {
            var current = calendarViewPager.currentItem
            Log.d(TAG, "오")
            calendarViewPager.setCurrentItem(current+1, false)
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

}