package com.awesomesol.peering.catDiary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R


class catDiaryFragment : Fragment() {

    val TAG="캣다"

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

        // ContentRVAdapter의 인자인 items 만들어주기
        val items = ArrayList<String>()
        items.add("01")
        items.add("02")
        items.add("03")
        items.add("04")
        val items2 = ArrayList<String>()
        items2.add("카테고고고리1")
        items2.add("카테고아리2")
        items2.add("카테고안리2")
        items2.add("카테고안리3")

        // rv의 adapter는 여기에서 만든 Adapter이다~
        rv.adapter = MonthListRVAdapter(items)
        rv2.adapter = CategoryRVAdapter(items2)

        rv.layoutManager = LinearLayoutManager(requireContext())
//        rv.layoutManager = GridLayoutManager(this, 6)
        rv2.layoutManager = LinearLayoutManager(requireContext())

        return view
    }


}