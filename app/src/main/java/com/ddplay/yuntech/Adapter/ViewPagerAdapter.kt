package com.ddplay.yuntech.Adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ddplay.yuntech.Fragment.HistoryFragment
import com.ddplay.yuntech.Fragment.HomeFragment
import com.ddplay.yuntech.Fragment.InfoFragment
import com.ddplay.yuntech.Fragment.MapFragment

class ViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int) = when(position) {
        0 -> HomeFragment()
        1 -> HistoryFragment()
        2 -> MapFragment()
        else -> InfoFragment()
    }
}