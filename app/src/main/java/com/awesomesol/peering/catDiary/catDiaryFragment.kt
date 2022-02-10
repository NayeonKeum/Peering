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
    val db = Firebase.firestore
    var year:Int = 0
    lateinit var tv_catDiaryFragment_year:TextView


    lateinit var dateGalleryData:HashMap<String, ArrayList<HashMap<String, Any>>>

    lateinit var rv2: RecyclerView
    lateinit var rv3: RecyclerView

    var categoryDataList = arrayListOf<String>()
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
        rv2 = view.findViewById<RecyclerView>(R.id.rv_catDiaryFragment_category)
        rv3 = view.findViewById<RecyclerView>(R.id.rv_catDiaryFragment_shareDiary)
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



//        if (groupDataList.size != 0) {
//            val groupDefault = view.findViewById<ConstraintLayout>(R.id.catDiaryFragment_default_container)
//            groupDefault.isVisible = false
//        }

//        rv.adapter = MonthListRVAdapter(items)
        rv2.adapter = CategoryRVAdapter(categoryDataList)
        rv3.adapter = ShareDiaryRVAdapter(groupDataList)

        //rv.layoutManager = GridLayoutManager(context, 6)
        rv2.layoutManager = LinearLayoutManager(requireContext())
        rv3.layoutManager = GridLayoutManager(context, 4)

        getFBCategoryData()
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

    private fun getFBCategoryData() {
        db.collection("users").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { result ->
                for (document in result){
                    Log.d(TAG, "${document.id} => ${document.data}")
                    Log.d(TAG, "유저 받기 성공!!")
                    val uid = document.data["uid"] as String
                    Log.d(TAG, uid)

                    // uid와 일치하는 카테고리 받아옴 (해당 유저의 카테고리)
                    db.collection("categories").document(uid)
                        .get()
                        .addOnSuccessListener { document ->
                            categoryDataList.clear()
                            Log.d(TAG, "${document.id} => ${document.data}")
                            val categories = document.data as HashMap<String, ArrayList<String>>
                            val list = categories["categoryList"]
                            Log.d(TAG, "리스트!!!!!!!! ${list?.get(0).toString()}")

//                            var categoryList = hashMapOf(
//                                "categoryList" to arrayListOf("놀기", "일상", "친구", "안녕", "반가워")
//                            )
//                            db.collection("categories").document(uid).set(categories)

                            if (list != null) {
                                categoryDataList = list
                            }
                            Log.d(TAG, categoryDataList.toString())

//                            CategoryRVAdapter(categoryDataList).notifyDataSetChanged()    // 리사이클러 뷰 갱신
                            rv2.adapter = CategoryRVAdapter(categoryDataList)
                            rv2.layoutManager = LinearLayoutManager(requireContext())
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun getFBGroupData() {
        db.collection("calendars").whereArrayContainsAny("uidList", arrayListOf(uid)).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("groups")
                        .get()
                        .addOnSuccessListener { result ->
                            groupDataList.clear()
                            for (document in result) {    // 가져온 문서들은 result에 들어감
                                val gid = "group" + Random().nextInt(10000)
                                val item = GroupInfo(
                                    document["groupName"] as String,
                                    document["groupNum"] as String,
                                    document["groupImg"] as String,
                                    document["cid"] as String,
                                    document["uidList"] as ArrayList<String>
                                )
//                    db.collection("groups").document(gid).set(item)
                                groupDataList.add(item)


                                //                    val group1 = hashMapOf(
                                //                        "groupName" to "죽여줘",
                                //                        "groupNum" to "4",
                                //                        "groupImg" to "img",
                                //                        "cid" to "uid",
                                //                        "uidList" to arrayListOf(1, 2, 3, 4)
                                //                    )
//
                            }
                            ShareDiaryRVAdapter(groupDataList).notifyDataSetChanged()    // 리사이클러 뷰 갱신
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}

