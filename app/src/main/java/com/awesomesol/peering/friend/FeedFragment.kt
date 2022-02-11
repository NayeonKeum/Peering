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
import com.awesomesol.peering.calendar.PostReadOnlyFragment
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
                val postreadonlyFragment = PostReadOnlyFragment()

                var cid=adapter.items[position].cid
                var uid=adapter.items[position].uid
                var nickname=adapter.items[position].nickname
                var mainImg=adapter.items[position].mainImg
                var profileImg=adapter.items[position].profileImg
                var content=adapter.items[position].content
                var publicScope=adapter.items[position].publicScope
                var category=adapter.items[position].category
                var dateym=adapter.items[position].date
                var type=adapter.items[position].type
                var group=adapter.items[position].isGroup

                Log.d(TAG, "mainImg, $mainImg")

                var date="${dateym.split("-")[0]}년 ${dateym.split("-")[1]}월 ${dateym.split("-")[2]}일"


                var bundle = Bundle()
                bundle.putString("date", date) //nn년 nn월 nn일
                bundle.putString("dateym", dateym) // 2022-02-02
                bundle.putString("cid", cid) // cid
                bundle.putSerializable("dateGalleryData", mainImg)
                bundle.putSerializable("content", content)
                bundle.putString("uid", uid) // 친구!!!!! uid임
                bundle.putString("nickname", nickname) //
                bundle.putString("profileImagePath", profileImg)
                bundle.putString("group", group.toString())

                postreadonlyFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_screen_panel, postreadonlyFragment).commit()
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

    // friend
    private fun friendBtnClick() {
        iv_FeedFragment_friends.setOnClickListener {
            val friendFragment = FriendFragment(uid)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, friendFragment).commit()

        }
    }

    private fun getFBFeedData(){
        val uid_list : ArrayList<String> = arrayListOf()

        // val ln0:Long=0
        val ln1:Long=1
        val ln2:Long=2

        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                val hmap = it.data?.get("friendList") as HashMap<String, Long>
                for (data in hmap){
                    if(data.value == ln1){
                        uid_list.add(data.key)
                    }
                }


                uid_list.add(uid)

                val feedRef = db.collection("feeds")
                feedRef.whereIn("uid", uid_list).orderBy("date",Query.Direction.DESCENDING).get()
                    .addOnSuccessListener { result ->

                        for (document in result){
                            Log.d(TAG, document.data["publicScope"].toString())
                            if(document.data["publicScope"]?.equals(ln1) == true  || document.data["publicScope"]?.equals(ln2) == true ){

                                // feedRef.orderBy("date", Query.Direction.DESCENDING)

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
