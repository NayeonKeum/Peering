package com.awesomesol.peering.friend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.calendar.CalendarMainFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {
    val TAG="피드"

    // 파이어스토어 객체 얻기, 얻은 FirebaseFirestore 객체로 컬렉션을 선택하고 문서를 추가하거나 가져오는 작업을 함
    val db = FirebaseFirestore.getInstance()   // Firestore 인스턴스 선언
    val feedDataList = arrayListOf<FeedModel>()   // 리스트 아이템 배열
    val adapter = FeedRVAdapter(feedDataList)     // 리사이클러 뷰 어댑터

    private lateinit var callback:OnBackPressedCallback

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
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "백프레스 눌름")
                val calendarFragment = CalendarMainFragment()
                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.main_screen_panel, calendarFragment)?.commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /*
        val feeds = db.collection("feeds")

        val feed1 = hashMapOf(
            "mainImg" to "a",
            "profileImg" to "b",
            "nickname" to "Lee",
            "content" to "반가워!!!! 이게 잘 되어야 할텐데....제발ㄹ...."
        )

        feeds.document("Feed_one").set(feed1)

        val feed2 = hashMapOf(
            "mainImg" to "c",
            "profileImg" to "d",
            "nickname" to "Kim",
            "content" to "hihihihihihiihihihi 안녕 반가워~!!~!!~!!~!!"
        )

        feeds.document("Feed_two").set(feed2)

        val feed3 = hashMapOf(
            "mainImg" to "e",
            "profileImg" to "f",
            "nickname" to "Cho",
            "content" to "이건 세 번째 리사이클러뷰 item에 들어갈 내용이다~~!!!!! 일단 오케이오케이...!!!!!"
        )

        feeds.document("Feed_three").set(feed3)

        val feed4 = hashMapOf(
            "mainImg" to "g",
            "profileImg" to "h",
            "nickname" to "Park",
            "content" to "더이상 무슨 말을 해야 할 지 모르겠다 이걸로 끄으으으으으으ㅡㅌ"
        )

        feeds.document("Feed_four").set(feed4)

        val feed5 = hashMapOf(
            "mainImg" to "g",
            "profileImg" to "h",
            "nickname" to "Yang",
            "content" to "끝인 줄 알았지만 일단 몇 개 더 추가할 것이다!"
        )

        feeds.document("Feed_five").set(feed5)*/

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rv_FeedFragment)

        rv.layoutManager = LinearLayoutManager(requireContext())

        // adapter에서 item을 클릭할 경우 FeedFragment으로 넘어가는 코드
        adapter.itemClick = object : FeedRVAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {
                val diaryreadFragment = DiaryReadFragment()

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_screen_panel, diaryreadFragment).commit()
            }
        }
        rv.adapter = adapter

        getFBFeedData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

   private fun getFBFeedData(){
        db.collection("feeds")    // 작업할 컬렉션
            .get()                   // 문서 가져오기
            .addOnSuccessListener { result ->
                // 성공할 경우
                Log.d(TAG, "result $result")
                feedDataList.clear()
                for (document in result){    // 가져온 문서들은 result에 들어감
                    val item = FeedModel(
                            document["cid"] as String,
                            document["uid"] as String,
                            document["nickname"] as String,
                            document["mainImg"] as ArrayList<HashMap<String, Any>>,
                            document["profileImg"] as String,
                            document["content"] as String,
                        document["publicScope"] as Long,
                        document["category"] as String,
                        document["date"] as String,
                        document["type"] as Long,
                        document["group"] as Long)

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

