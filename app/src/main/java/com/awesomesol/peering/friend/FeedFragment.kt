package com.awesomesol.peering.friend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
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
            //val diaryreadFragment = DiaryReadFragment()
            val friendFragment = FriendFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, friendFragment).commitNow()
        }
    }
    
     // todo : 임의로 대입한 값이 아닌 실제로 작성한 값이 들어갈 수 있도록 하자..!!!
    // recyclerview multi 찾아서 mainImg 데이터 전달받도록 수정하기 -> viewholder 여러 개
   private fun getFBFeedData(){
        db.collection("feeds")    // 작업할 컬렉션
            .get()                   // 문서 가져오기
            .addOnSuccessListener { result ->
                // 성공할 경우
                feedDataList.clear()
                for (document in result){    // 가져온 문서들은 result에 들어감
                    val item = FeedModel(
                            document["cid"] as String,
                            document["uid"] as String,
                            document["nickname"] as String,
                            document["mainImg"] as String,
                            document["profileImg"] as String,
                            document["content"] as String)

                    feedDataList.add(item)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                adapter.notifyDataSetChanged()    // 리사이클러 뷰 갱신
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

   }
}
