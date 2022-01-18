package com.awesomesol.peering.feed

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.activity.FeedWriteActivity
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rv_FeedFragment)

        val items = ArrayList<FeedModel>()
        items.add(FeedModel("a", "b", "Kim", "안녕"))
        items.add(FeedModel("a", "b", "Lee", "hihihi"))
        items.add(FeedModel("a", "b", "Kang", "hello"))
        items.add(FeedModel("a", "b", "Cho", "hey"))
        items.add(FeedModel("a", "b", "Park", "yoyo"))

        rv.adapter = FeedRVAdapter(items)
        rv.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        writeBtnClick()
    }
    fun writeBtnClick(){
        iv_FeedFragment_write.setOnClickListener {
            val intent = Intent(getActivity(), FeedWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // activity back stack 모두 제거
            startActivity(intent)
        }
    }

}