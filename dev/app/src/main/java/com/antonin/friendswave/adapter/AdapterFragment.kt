package com.antonin.friendswave.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.antonin.friendswave.ui.fragment.HomeFragment

class AdapterFragment(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return HomeFragment()
            }
//            1 -> {
//                return MyThirdFragment()
//            }
//            2 -> {
//                return MyHomeFragment()
//
//            }
//            3  -> return NotificationsFragment()
        }
        return HomeFragment()
    }
}