package com.awesomesol.peering.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.awesomesol.peering.R
import com.awesomesol.peering.calendar.CalendarFragment
import com.awesomesol.peering.character.CharacterFragment
import com.awesomesol.peering.databinding.ActivityMainBinding
import com.awesomesol.peering.feed.FeedFragment
import com.awesomesol.peering.friend.FriendFragment
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMainBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 바인딩
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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