package com.antonin.friendswave.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.antonin.friendswave.ui.fragmentMain.ContactFragment
import com.antonin.friendswave.ui.fragmentMain.EventFragment
import com.antonin.friendswave.ui.fragmentMain.HomeFragment

class AdapterFragment(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3
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
        }
        return HomeFragment()
    }
}