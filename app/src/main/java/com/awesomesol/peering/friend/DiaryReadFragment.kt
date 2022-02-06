package com.awesomesol.peering.friend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.awesomesol.peering.R
import com.awesomesol.peering.calendar.GalleryData
import com.awesomesol.peering.calendar.PostFragment
import com.awesomesol.peering.calendar.PostInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.HashMap

class DiaryReadFragment : Fragment() {
    val TAG="readDiary"

    // 파이어스토어 객체 얻기, 얻은 FirebaseFirestore 객체로 컬렉션을 선택하고 문서를 추가하거나 가져오는 작업을 함
    val db = FirebaseFirestore.getInstance()   // Firestore 인스턴스 선언
    private var uid:String=""
    private var title:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_diary_read, container, false)

        getFBPostData()
        return view
    }
    // 아직 확실하지 않음..!! -> 다시 한 번 확인 필요
    private fun getFBPostData(){
        lateinit var postData: HashMap<String, ArrayList<PostInfo>>
        db.collection("posts").whereArrayContainsAny("uid", arrayListOf(uid)).get()
            .addOnSuccessListener { result ->
                for (document in result) {    // 가져온 문서들은 result에 들어감

                    Log.d(TAG, "${document.id} => ${document.data}")
                    if (document.data["title"].toString().equals(title)){
                        postData = document.data["category"] as HashMap<String, ArrayList<PostInfo>>
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}