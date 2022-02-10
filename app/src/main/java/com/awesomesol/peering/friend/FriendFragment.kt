package com.awesomesol.peering.friend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.calendar.CalendarMainFragment
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_friend.*

class FriendFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_friend, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rv_FriendFragment)

        val items = ArrayList<FriendModel>()
        items.add(FriendModel("a", "b",  "c", "d","Kim", "abc@gmail.com"))
        items.add(FriendModel("a", "b", "c", "d","Lee", "abc@gmail.com"))
        items.add(FriendModel("a", "b", "c", "d","Kang", "abc@gmail.com"))
        items.add(FriendModel("a", "b", "c", "d","Cho", "abc@gmail.com"))
        items.add(FriendModel("a", "b", "c", "d","Park", "abc@gmail.com"))
        items.add(FriendModel("a", "b", "c", "d","Park", "abc@gmail.com"))
        items.add(FriendModel("a", "b", "c", "d","Park", "abc@gmail.com"))
        items.add(FriendModel("a", "b", "c", "d","Park", "abc@gmail.com"))

        rv.adapter = FriendRVAdapter(items)
        rv.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moreBtnClick()
    }
    // recyclerview 클릭 수정
    fun moreBtnClick(){
        iv_FriendFragment_friends.setOnClickListener {
            val diaryreadFragment = DiaryReadFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, diaryreadFragment).commitNow()
        }
    }


}