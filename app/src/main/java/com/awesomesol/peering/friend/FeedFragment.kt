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
import com.google.firebase.firestore.Query
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

    private var uid:String = ""

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
        arguments?.let {
            uid = it.getString("id").toString()

        }
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
        friendBtnClick()
    }

    // friends 버튼 클릭
    private fun friendBtnClick() {
        iv_FeedFragment_friends.setOnClickListener {
            val friendFragment = FriendFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, friendFragment).commit()

        }
    }

    private fun getFBFeedData(){
        val uid_list : ArrayList<String> = arrayListOf()
        val ln1:Long=0
        val ln2:Long=1
        val ln3:Long=2


        db.collection("users").document(uid).get()
            .addOnSuccessListener {

                val hmap = it.data?.get("friendList") as HashMap<String, Long>
                for (data in hmap){

                    if(data.value.equals(ln1) == true){
                        uid_list.add(data.key)
                        Log.d(TAG,uid_list[0])
                    }
                }
                val feedRef = db.collection("feeds")
                feedRef.whereIn("uid", uid_list).get()
                    .addOnSuccessListener { result ->
                        Log.d(TAG, "result ${result}")

                        for (document in result){
                            Log.d(TAG, document.data["publicScope"].toString())
                            if(document.data["publicScope"]?.equals(ln2) == true  || document.data["publicScope"]?.equals(ln3) == true ){

                                feedRef.orderBy("date", Query.Direction.DESCENDING)

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
                        }
                        adapter.notifyDataSetChanged()    // 리사이클러 뷰 갱신
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }
    }
}
