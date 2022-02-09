package com.awesomesol.peering.catDiary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class catDiaryFragment : Fragment() {

    val TAG="캣다"

    val db = Firebase.firestore
    val groupDataList = arrayListOf<GroupInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val groups = db.collection("groups")

        val gid= "group"+ Random().nextInt(10000)

        val group1 = hashMapOf(
            "groupName" to "이제 된 건가",
            "groupNum" to "4",
            "groupImg" to "img",
            "cid" to "uid",
            "uidList" to arrayListOf(1, 2, 3, 4)
        )
        groups.document(gid).set(group1)

//        db.collection("groups")
//            .add(group1)
//            .addOnSuccessListener {
//                Log.d(TAG, "성공!!")
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
//            }
//
//        db.collection("groups")
//            .get()
//            .addOnSuccessListener {
//                Log.d(TAG, "받기 성공!!")
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_catdiary, container, false)

        // RecyclerView 생성
        val rv = view.findViewById<RecyclerView>(R.id.rv_catDiaryFragment_monthList)
        val rv2 = view.findViewById<RecyclerView>(R.id.rv_catDiaryFragment_category)
        val rv3 = view.findViewById<RecyclerView>(R.id.rv_catDiaryFragment_shareDiary)

        // ContentRVAdapter의 인자인 items 만들어주기
        val items = ArrayList<String>()
        items.add("01")
        items.add("02")
        items.add("03")
        items.add("04")
        items.add("01")
        items.add("02")
        items.add("03")
        items.add("04")
        items.add("01")
        items.add("02")
        items.add("03")
        items.add("04")
        val items2 = ArrayList<String>()
        items2.add("카테고고고리1")
        items2.add("카테고아리2")
        items2.add("카테고안리2")
        items2.add("카테고안리3")


        if (groupDataList.size != 0) {
            val groupDefault = view.findViewById<ConstraintLayout>(R.id.catDiaryFragment_default_container)
            groupDefault.isVisible = false
        }

        rv.adapter = MonthListRVAdapter(items)
        rv2.adapter = CategoryRVAdapter(items2)
        rv3.adapter = ShareDiaryRVAdapter(groupDataList)

        rv.layoutManager = GridLayoutManager(context, 6)
        rv2.layoutManager = LinearLayoutManager(requireContext())
        rv3.layoutManager = GridLayoutManager(context, 4)

        getFBGroupData()
        return view
    }

    private fun getFBGroupData() {
        db.collection("groups")
            .get()
            .addOnSuccessListener { result ->
                groupDataList.clear()
                for (document in result){    // 가져온 문서들은 result에 들어감
                    val item = GroupInfo(
                        document["groupName"] as String,
                        document["groupNum"] as String,
                        document["groupImg"] as String,
                        document["cid"] as String,
                        document["uidList"] as ArrayList<String>)
                    groupDataList.add(item)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                ShareDiaryRVAdapter(groupDataList).notifyDataSetChanged()    // 리사이클러 뷰 갱신
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

}