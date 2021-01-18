package com.example.buddy_hubb.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.buddy_hubb.fragments.InboxFragment
import com.example.buddy_hubb.fragments.PeopleFragment
import com.example.buddy_hubb.fragments.SettingFragment

class ScreenSliderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int =3

    override fun createFragment(position: Int): Fragment =when(position)
    {
        0-> InboxFragment()
        1-> PeopleFragment()
        else-> SettingFragment()
    }


}
