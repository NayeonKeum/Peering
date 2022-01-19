package com.awesomesol.peering.catDiary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.awesomesol.peering.R
import com.kakao.sdk.talk.TalkApiClient


class catDiaryFragment : Fragment() {
    // 우리 친구... 이메일로 초대 보내느건가?!?! 어떻게 보내는 거였지...

    val TAG="캣다"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catdiary, container, false)
    }

}