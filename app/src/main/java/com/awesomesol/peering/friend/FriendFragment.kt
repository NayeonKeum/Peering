package com.awesomesol.peering.friend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.awesomesol.peering.R
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.usermgmt.UserManagement


class FriendFragment : Fragment() {
    
    val TAG="친구"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카카오톡 친구 목록 가져오기 (기본)
        TalkApiClient.instance.friends { friends, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error)
            }
            else if (friends != null) {
                Log.i(TAG, "카카오톡 친구 목록 가져오기 성공 \n${friends.elements.joinToString("\n")}")

                // 친구의 UUID 로 메시지 보내기 가능
            }
        }




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

}