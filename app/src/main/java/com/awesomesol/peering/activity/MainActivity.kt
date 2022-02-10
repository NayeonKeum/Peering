package com.awesomesol.peering.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.awesomesol.peering.R
import com.awesomesol.peering.calendar.CalendarMainFragment
import com.awesomesol.peering.catDiary.GroupCalMainFragment
import com.awesomesol.peering.friend.FriendCalMainFragment
import com.awesomesol.peering.catDiary.catDiaryFragment
import com.awesomesol.peering.character.CharacterFragment
import com.awesomesol.peering.databinding.ActivityMainBinding
import com.awesomesol.peering.friend.FeedFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.template.model.*
import com.kakao.sdk.user.UserApiClient
import nl.joery.animatedbottombar.AnimatedBottomBar
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
    var mfriendList: HashMap<String, Int> = hashMapOf()

    lateinit var myCalID:String
    lateinit var myCalName:String


    val TAG="메인"
    val fs= Firebase.firestore



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 바인딩
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        // 메세지 보내기
//        val defaultFeed = FeedTemplate(
//            content = Content(
//                title = "Peering",
//                description = "순간을 기억해주는 당신만의 앱",
//                imageUrl = "https://firebasestorage.googleapis.com/v0/b/peering-58c65.appspot.com/o/applogo.png?alt=media&token=99d9aa01-ddae-45e6-8d9b-fee0a0ad3528",
//                link = Link(
//                    webUrl = "https://developers.kakao.com",
//                    mobileWebUrl = "https://developers.kakao.com"
//                )
//            ),
//            buttons = listOf(
//                Button(
//                    "앱으로 보기",
//                    Link(
//                        androidExecParams = hashMapOf("key1" to "value1", "key2" to "value2"),
//                        iosExecParams = hashMapOf("key1" to "value1", "key2" to "value2")
//                    )
//                )
//            )
//        )
//
//
//        LinkClient.instance.defaultTemplate(this, defaultFeed) { linkResult, error ->
//            if (error != null) {
//                Log.e(TAG, "카카오링크 보내기 실패", error)
//            }
//            else if (linkResult != null) {
//                Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
//                startActivity(linkResult.intent)
//
//                // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
//                Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
//                Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
//            }
//        }


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
                        val calendarFragment= FriendCalMainFragment()
                        // val calendarFragment= GroupCalMainFragment()
                        //val calendarFragment = CalendarMainFragment()
                        // val calendarFragment = PostFragment()

                        val userBundle = Bundle()
                        userBundle.putString("id", uid)
                        userBundle.putString("email", email)
                        userBundle.putString("nickname", nickname)
                        userBundle.putString("profileImagePath", profileImagePath)
                        calendarFragment.arguments = userBundle

                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, calendarFragment).commit()
                    }
                    1 -> {
                        val friendFragment = catDiaryFragment()
                        val userBundle = Bundle()
                        userBundle.putString("id", uid)
                        friendFragment.arguments = userBundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, friendFragment).commit()
                    }

                    2 -> {
                        val feedFragment = FeedFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, feedFragment).commit()
                    }
                    3 -> {
                        val characterFragment = CharacterFragment()
                        val userBundle = Bundle()
                        userBundle.putString("id", uid)
                        userBundle.putString("email", email)
                        userBundle.putString("nickname", nickname)
                        userBundle.putString("profileImagePath", profileImagePath)
                        characterFragment.arguments = userBundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, characterFragment).commit()
                    }
                }
            }
        })
        kakaoDataCallback{
            val calendarFragment= FriendCalMainFragment()
            // val calendarFragment= GroupCalMainFragment()
            //val calendarFragment = CalendarMainFragment()
            // val calendarFragment = PostFragment()

            val userBundle = Bundle()
            userBundle.putString("id", uid)
            userBundle.putString("email", email)
            userBundle.putString("nickname", nickname)
            userBundle.putString("profileImagePath", profileImagePath)
            calendarFragment.arguments = userBundle
            supportFragmentManager.beginTransaction().replace(R.id.main_screen_panel, calendarFragment)
                .commit()
        }


    }

    fun kakaoDataCallback(callback:(String)->Unit){
        UserApiClient.instance.me { user, _ ->
            uid = user?.id.toString()
            nickname = user?.kakaoAccount?.profile?.nickname.toString()
            profileImagePath = user?.kakaoAccount?.profile?.profileImageUrl.toString()
            email = user?.kakaoAccount?.email.toString()
            callback(email)
        }
    }


    // 액티비티가 파괴될 때..
    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
    }


}
