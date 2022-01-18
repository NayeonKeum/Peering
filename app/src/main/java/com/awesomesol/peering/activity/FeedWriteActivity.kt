package com.awesomesol.peering.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.awesomesol.peering.R
import com.awesomesol.peering.databinding.ActivityFeedWriteBinding
import com.awesomesol.peering.feed.FeedFragment

class FeedWriteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFeedWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFeedWriteActivityFinish.setOnClickListener {
            val feedFragment = FeedFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, feedFragment).commit()
        }
    }
}