package com.awesomesol.peering.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.awesomesol.peering.R
import com.awesomesol.peering.calendar.CalendarFragment
import com.awesomesol.peering.character.CharacterFragment
import com.awesomesol.peering.databinding.ActivityMainBinding
import com.awesomesol.peering.feed.FeedFragment
import com.awesomesol.peering.friend.FriendFragment
import nl.joery.animatedbottombar.AnimatedBottomBar
import com.awesomesol.peering.calendar.CommentInfo
import com.awesomesol.peering.calendar.PostInfo
import com.awesomesol.peering.character.UserInfo
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMainBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    lateinit var uid:String
    lateinit var email:String
    lateinit var nickname:String
    lateinit var profileImagePath:String

    val TAG="메인"
    val fs= Firebase.firestore


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 바인딩
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        UserApiClient.instance.me { user, error ->
            uid = user?.id.toString()
            nickname = user?.kakaoAccount?.profile?.nickname.toString()
            profileImagePath = user?.kakaoAccount?.profile?.profileImageUrl.toString()
            email = user?.kakaoAccount?.email.toString()


            val user= UserInfo(uid, nickname, profileImagePath, email)

            fs.collection("users").document(uid).set(user)
                .addOnSuccessListener { Log.d(TAG, "fs 에 유저 정보 저장 쨘") }
                .addOnFailureListener{e-> Log.d(TAG, "에러 났음 쨘", e)}
        }




        /* 여기는 전부 firestore 사용 예시~ */
//
//        // 조인 예시!
//        fs.collection("user").whereEqualTo("nickName", "금나연").get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
//
//
//
//        // POST에 정보 저장+id도 함께 저장
//        val post=PostInfo("", uid,"제목 예시", LocalDate.of(2022, 1, 1).toString(), "내용 글 예시")//, "".toUri())
//        var pid=""
//
//        fs.collection("posts").add(post)
//            .addOnSuccessListener {
//                Log.d(TAG, "fs 에 post 정보 저장 쨘, id : "+ it.id)
//                pid=it.id
//                it.update("pid", pid)
//                    .addOnSuccessListener {
//                        Log.d(TAG, "fs 에 pid 정보 저장 쨘")
//                    }
//                    .addOnFailureListener{
//                        e-> Log.d(TAG, "pid 에러 났음 쨘", e)
//                    }
//                }
//            .addOnFailureListener{e-> Log.d(TAG, "에러 났음 쨘", e)}
//
//
//        // 불러오기
//        fs.collection("posts").whereEqualTo("pid", pid).get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
//
//        // 댓글
//        var cid=""
//        var pid2="U4gs20Zxrl5gVerBjrTY"
//        val comment= CommentInfo(cid, pid2, "댓글 예시3", LocalDateTime.now().toString())
//
//        fs.collection("comments").add(comment)
//            .addOnSuccessListener {
//                Log.d(TAG, "2fs 에 comment 정보 저장 쨘")
//                cid=it.id
//                it.update("cid", cid)
//                    .addOnSuccessListener {
//                        Log.d(TAG, "fs 에 cid 정보 저장 쨘")
//                    }
//                    .addOnFailureListener{
//                            e-> Log.d(TAG, "cid 에러 났음 쨘", e)
//                    }
//            }
//            .addOnFailureListener{e-> Log.d(TAG, "2에러 났음 쨘", e)}
//
//        // 코멘트 불러오기
//        fs.collection("comments").whereEqualTo("pid", pid2).get()
//            .addOnSuccessListener { documents ->
//                var comhash: HashMap<String, Any> = hashMapOf()
//                for (document in documents) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                    comhash.put(document.id,document.data)
//                }
//                Log.d(TAG, "comhash: "+comhash.toString())
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
//


        // Bottom Navigation
        binding.bottomNavigation.setOnTabSelectListener(object :
                AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                    lastIndex: Int,
                    lastTab: AnimatedBottomBar.Tab?,
                    newIndex: Int,
                    newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> {
                        val calendarFragment = CalendarFragment()
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.main_screen_panel, calendarFragment).commit()
                    }
                    1 -> {
                        val feedFragment = FeedFragment()
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.main_screen_panel, feedFragment).commit()
                    }

                    2 -> {
                        val friendFragment = FriendFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, friendFragment).commit()

                    }
                    3 -> {
                        val characterFragment = CharacterFragment()
                        val userBundle = Bundle()
                        userBundle.putString("id", uid)
                        userBundle.putString("email", email)
                        userBundle.putString("nickname", nickname)
                        userBundle.putString("profileImagePath", profileImagePath)
                        characterFragment.arguments =userBundle
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.main_screen_panel, characterFragment).commit()
                    }
                }
            }
        })

        val calendarFragment = CalendarFragment()
        supportFragmentManager.beginTransaction().replace(R.id.main_screen_panel, calendarFragment)
                .commit()

    }

    // 액티비티가 파괴될 때..
    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
    }

}