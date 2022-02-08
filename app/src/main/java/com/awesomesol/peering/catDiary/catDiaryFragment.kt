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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val db = Firebase.firestore
        val groups = db.collection("groups")

        val group1 = hashMapOf(
            "groupName" to "그룹명",
            "groupNum" to "6",
            "groupImg" to "img",
            "uid" to "uid",
            "profileImg" to "profileImg"
        )
        groups.document("groupOne").set(group1)

        val group2 = hashMapOf(
            "groupName" to "그룹명2",
            "groupNum" to "4",
            "groupImg" to "img",
            "uid" to "uid",
            "profileImg" to "profileImg"
        )
        groups.document("groupTwo").set(group2)

        db.collection("groups")
            .add(group1)
            .addOnSuccessListener {
                Log.d(TAG, "성공!!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        db.collection("groups")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "받기 성공!!")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


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

        val groupDataList = arrayListOf<GroupInfo>()

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

        return view
    }


}