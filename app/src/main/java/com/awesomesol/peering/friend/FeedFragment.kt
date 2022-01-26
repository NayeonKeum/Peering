package com.awesomesol.peering.friend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.FeedWriteActivity
import com.awesomesol.peering.calendar.CalendarFragment
import com.bumptech.glide.Glide
import com.kakao.sdk.talk.TalkApiClient
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.feed_rv_item.*
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {
    val TAG="피드"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // 카카오톡 친구 목록 가져오기 (기본)
//        TalkApiClient.instance.friends { friends, error ->
//            if (error != null) {
//                Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error)
//            }
//            else if (friends != null) {
//                Log.i(TAG, "카카오톡 친구 목록 가져오기 성공 \n${friends.elements.joinToString("\n")}")
//                Log.d(TAG, friends.toString())
//                // 친구의 UUID 로 메시지 보내기 가능
//            }
//        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rv_FeedFragment)

        val items = ArrayList<FeedModel>()
        items.add(FeedModel("a", "b", "Kim", "오늘은 따사로운 햇살과 함께 코딩을 했다. 낭만 속의 비낭만..... 소리 없는 아우성..... 집인데 집에 가고 싶었다...하하핳"))
        items.add(FeedModel("a", "b", "Lee", "hihihi"))
        items.add(FeedModel("a", "b", "Kang", "hello"))
        items.add(FeedModel("a", "b", "Cho", "hey"))
        items.add(FeedModel("a", "b", "Park", "yoyo"))

        rv.adapter = FeedRVAdapter(items)
        rv.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moreBtnClick()
    }
    // recyclerview 클릭 수정
    fun moreBtnClick(){
        iv_FeedFragment_friends.setOnClickListener {
            val diaryreadFragment = DiaryReadFragment()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, diaryreadFragment).commitNow()
        }
    }


}