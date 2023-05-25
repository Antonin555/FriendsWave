package com.antonin.friendswave.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.antonin.friendswave.ui.event.EventsSubscribeFragment
import com.antonin.friendswave.ui.event.MyEventFragment

// Documentation https://developer.android.com/reference/androidx/fragment/app/FragmentPagerAdapter

class AdapterFragmentEvent(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return MyEventFragment()
            }
            1 -> {
                return EventsSubscribeFragment()
            }


        }
       return MyEventFragment()
    }
}