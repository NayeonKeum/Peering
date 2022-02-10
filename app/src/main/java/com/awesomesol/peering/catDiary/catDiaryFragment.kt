package com.awesomesol.peering.catDiary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class catDiaryFragment : Fragment() {

    val TAG="캣다"

    var uid:String=""
    var cid:String=""
    var fs= Firebase.firestore
    var year:Int = 0
    lateinit var tv_catDiaryFragment_year:TextView


    lateinit var dateGalleryData:HashMap<String, ArrayList<HashMap<String, Any>>>

    val db = Firebase.firestore
    val groupDataList = arrayListOf<GroupInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_catdiary, container, false)

        // RecyclerView 생성
        val rv = view.findViewById<RecyclerView>(R.id.rv_catDiaryFragment_monthList)
        val rv2 = view.findViewById<RecyclerView>(R.id.rv_catDiaryFragment_category)
        val rv3 = view.findViewById<RecyclerView>(R.id.rv_catDiaryFragment_shareDiary)
        tv_catDiaryFragment_year=view.findViewById(R.id.tv_catDiaryFragment_year)
        val iv_catDiaryFragment_leftarr=view.findViewById<ImageView>(R.id.iv_catDiaryFragment_leftarr)
        val iv_catDiaryFragment_rightarr=view.findViewById<ImageView>(R.id.iv_catDiaryFragment_rightarr)

        var year_monthList:HashMap<String, Int> = hashMapOf()

        arguments?.let {
            // 테스트
            uid = it.getString("id").toString()
        }

        calendarCallback { datesList->

            for (date in datesList) {
                var datearr=date.split("-") // [y, m, d]
                val ym=datearr[0]+"-"+datearr[1]
                val day:Int=datearr[2].toInt()

                if (year_monthList[ym]==null) {
                    year_monthList[ym]=1
                }else{
                    year_monthList[ym] = year_monthList[ym] as Int +1
                }
            }
            Log.d(TAG, "year_monthList, $year_monthList")

            val today: LocalDate = LocalDate.now()
            year=today.year
            tv_catDiaryFragment_year.text=year.toString() +"년"
            rv.adapter = MonthListRVAdapter(year_monthList, year)
            rv.layoutManager = GridLayoutManager(context, 6)

            iv_catDiaryFragment_leftarr.setOnClickListener{
                year-=1
                tv_catDiaryFragment_year.text=year.toString() +"년"
                rv.adapter = MonthListRVAdapter(year_monthList, year)
                rv.layoutManager = GridLayoutManager(context, 6)
            }
            iv_catDiaryFragment_rightarr.setOnClickListener{
                year+=1
                tv_catDiaryFragment_year.text=year.toString() +"년"
                rv.adapter = MonthListRVAdapter(year_monthList, year)
                rv.layoutManager = GridLayoutManager(context, 6)
            }

        }


        // 그룹
//        val gid= "group"+ Random().nextInt(10000)
//        val group1 = hashMapOf(
//            "groupName" to "이제 된 건가",
//            "groupNum" to "4",
//            "groupImg" to "img",
//            "cid" to "uid",
//            "uidList" to arrayListOf(1, 2, 3, 4)
//        )
//        db.collection("groups").document(gid).set(group1)
//
//        db.collection("groups")
//            .get()
//            .addOnSuccessListener {
//                Log.d(TAG, "받기 성공!!")
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }


        db.collection("users").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { result ->
                for (document in result){
                    Log.d(TAG, "${document.id} => ${document.data}")
                    Log.d(TAG, "유저 받기 성공!!")
                    val uid = document.data["uid"] as String
                    Log.d(TAG, uid)
                    var categoryList = hashMapOf(
                        "categoryList" to arrayListOf("놀기", "일상", "친구", "안녕")
                    )
                    db.collection("categories").document(uid).set(categoryList)

                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }



        db.collection("categories")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "카테고리 받기 성공!!")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }




        val items2 = ArrayList<String>()
        items2.add("카테고고고리1")
        items2.add("카테고아리2")
        items2.add("카테고안리2")
        items2.add("카테고안리3")
        val items3 = ArrayList<String>()
        items3.add("솔룩스")
        items3.add("어썸솔 팟팅")
        items3.add("인생은 뭘까")
        items3.add("하하")
        items3.add("글자를길게써봅시다")


//        if (groupDataList.size != 0) {
//            val groupDefault = view.findViewById<ConstraintLayout>(R.id.catDiaryFragment_default_container)
//            groupDefault.isVisible = false
//        }

//        rv.adapter = MonthListRVAdapter(items)
        rv2.adapter = CategoryRVAdapter(items2)
        rv3.adapter = ShareDiaryRVAdapter(groupDataList)

        //rv.layoutManager = GridLayoutManager(context, 6)
        rv2.layoutManager = LinearLayoutManager(requireContext())
        rv3.layoutManager = GridLayoutManager(context, 4)

        getFBGroupData()
        return view
    }

    fun calendarCallback(callback:(MutableSet<String>)->Unit){
        fs.collection("users").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //Log.d(TAG, "${document.id} => ${document.data}")
                    val hh= document.data["calendarID"] as HashMap<String, String>
                    cid= hh["myCalendar"].toString()

                    fs.collection("calendars").document(cid).get()
                        .addOnSuccessListener {
                            dateGalleryData= it.data?.get("dataList4") as HashMap<String, ArrayList<HashMap<String, Any>>>
                            Log.d(TAG, "dateGalleryData.keys, ${dateGalleryData.keys}")
                            callback(dateGalleryData.keys)

                        }
                        .addOnFailureListener{
                            Log.d(TAG, "datalist4 remains null")
                        }

                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
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