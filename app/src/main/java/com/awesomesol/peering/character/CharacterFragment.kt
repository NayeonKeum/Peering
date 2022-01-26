package com.awesomesol.peering.character

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.awesomesol.peering.R
import com.bumptech.glide.Glide

class CharacterFragment : Fragment() {

    lateinit var id:String
    lateinit var email:String
    lateinit var nickname:String
    lateinit var profileImagePath: String
    lateinit var followingNum: String
    lateinit var followerNum:String
    lateinit var progressNow: String
    lateinit var dDay:String
    lateinit var diaryCnt:String
    lateinit var diaryLeftCnt: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getString("id").toString()
            email = it.getString("email").toString()
            nickname = it.getString("nickname").toString()
            profileImagePath = it.getString("profileImagePath").toString()
            //나중에 데이터 제대로 연결해야함
            progressNow = "70"
            followingNum = "140"
            followerNum = "200"
            dDay = "100"
            diaryCnt="10"
            diaryLeftCnt="2"
        }

        Log.e("카카오 캐릭", id)
        Log.e("카카오 캐릭", email)
        Log.e("카카오 캐릭", nickname)
        Log.e("카카오 캐릭", profileImagePath)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var viewFrag=inflater.inflate(R.layout.fragment_character, container, false)
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_nickname)?.text=nickname
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_email)?.text=email

        viewFrag?.findViewById<ImageView>(R.id.iv_CharacterFragment_profileImg)?.let {
            Glide.with(viewFrag)
                .load(profileImagePath)
                .into(it)
        }

        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_followingNum)?.text=followingNum
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_followerNum)?.text=followerNum
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_dDay)?.text=dDay
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_diaryCnt)?.text=diaryCnt
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_diaryLeftCnt)?.text=diaryLeftCnt
        viewFrag?.findViewById<ProgressBar>(R.id.progressBar_CharacterFragment)?.progress=progressNow.toInt()

        return viewFrag
    }

}