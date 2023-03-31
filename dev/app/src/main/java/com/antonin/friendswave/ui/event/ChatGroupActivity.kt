package com.antonin.friendswave.ui.event

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.AdapterFragmentChatGroup
import com.antonin.friendswave.adapter.AdapterFragmentEvent
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChatGroupActivity : AppCompatActivity() {
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2

    private lateinit var adapterFragment : AdapterFragmentChatGroup

    private lateinit var viewModel : EventFragmentViewModel


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group)

        viewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))

        val tabLayoutArray = arrayOf(
            "Event",
            "ChatGroup",
        )


        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        adapterFragment = AdapterFragmentChatGroup(this)
        viewPager.adapter = adapterFragment
        tabLayout.getTabAt(0)!!.orCreateBadge.backgroundColor
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabLayoutArray[position]
        }.attach()

    }
}