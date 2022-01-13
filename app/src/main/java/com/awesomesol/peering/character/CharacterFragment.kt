package com.awesomesol.peering.character

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.awesomesol.peering.R
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient

class CharacterFragment : Fragment() {

    lateinit var id:String
    lateinit var email:String
    lateinit var nickname:String
    lateinit var profileImagePath: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getString("id").toString()
            email = it.getString("email").toString()
            nickname = it.getString("nickname").toString()
            profileImagePath = it.getString("profileImagePath").toString()
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
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_nickname)?.setText(nickname)
        viewFrag?.findViewById<TextView>(R.id.tv_CharacterFragment_email)?.setText(email)

        activity?.let {
            viewFrag?.findViewById<ImageView>(R.id.iv_CharacterFragment_profileImg)?.let { it1 ->
                Glide.with(it)
                    .load(profileImagePath)
                    .into(it1)
            }
        }

        return viewFrag
    }

}