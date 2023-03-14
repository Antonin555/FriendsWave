package com.antonin.friendswave.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.antonin.friendswave.ui.fragment.ContactFragment
import com.antonin.friendswave.ui.fragment.EventFragment
import com.antonin.friendswave.ui.fragment.HomeFragment
import com.antonin.friendswave.ui.fragment.NotifsFragment

class AdapterFragment(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return HomeFragment()
            }
            1 -> {
                return EventFragment()
            }
            2 -> {
                return ContactFragment()
            }
            3 -> {
                return NotifsFragment()
            }


        }
        return HomeFragment()
    }
}