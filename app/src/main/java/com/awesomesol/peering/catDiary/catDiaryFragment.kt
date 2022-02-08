package com.awesomesol.peering.catDiary

import android.os.Bundle
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
        val items3 = ArrayList<String>()
        items3.add("솔룩스")
        items3.add("어썸솔 팟팅")
        items3.add("인생은 뭘까")
        items3.add("하하")
        items3.add("글자를길게써봅시다")

        if (items3.size != 0) {
            val groupDefault = view.findViewById<ConstraintLayout>(R.id.catDiaryFragment_default_container)
            groupDefault.isVisible = false
        }

        // rv의 adapter는 여기에서 만든 Adapter이다~
        rv.adapter = MonthListRVAdapter(items)
        rv2.adapter = CategoryRVAdapter(items2)
        rv3.adapter = ShareDiaryRVAdapter(items3)

        rv.layoutManager = GridLayoutManager(context, 6)
        rv2.layoutManager = LinearLayoutManager(requireContext())
        rv3.layoutManager = GridLayoutManager(context, 4)

        return view
    }


}