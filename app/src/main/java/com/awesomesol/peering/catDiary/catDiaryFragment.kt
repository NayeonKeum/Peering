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

        // ContentRVAdapter의 인자인 items 만들어주기
        val items = ArrayList<String>()
        items.add("a")
        items.add("b")
        items.add("c")

        // rv의 adapter는 여기에서 만든 Adapter이다~
        rv.adapter = MonthListRVAdapter(items)

        rv.layoutManager = LinearLayoutManager(requireContext())
//        rv.layoutManager = GridLayoutManager(this, 6)

        return view
    }


}