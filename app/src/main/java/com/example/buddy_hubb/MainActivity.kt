package com.example.buddy_hubb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.buddy_hubb.adapter.ScreenSliderAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewPager.adapter= ScreenSliderAdapter(this)
        TabLayoutMediator(tabs,viewPager,TabLayoutMediator.TabConfigurationStrategy{ tab: TabLayout.Tab, pos: Int ->
            when(pos)
            {
                0->tab.text="CHATS"
                1->tab.text="PEOPLE"
                2->tab.text="SETTING"
            }

        }).attach()
    }
}