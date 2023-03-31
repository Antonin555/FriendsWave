package com.antonin.friendswave.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.antonin.friendswave.ui.chat.ChatGroupFragment
import com.antonin.friendswave.ui.event.MyEventFragment

class AdapterFragmentChatGroup(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return MyEventFragment()
            }
            1 -> {
                return ChatGroupFragment()
            }


        }
        return MyEventFragment()
    }
}