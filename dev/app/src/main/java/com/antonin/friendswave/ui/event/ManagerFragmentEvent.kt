package com.antonin.friendswave.ui.event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.AdapterFragmentEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ManagerFragmentEvent : AppCompatActivity() {


    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2

    private lateinit var adapterFragment : AdapterFragmentEvent

    private lateinit var viewModel : EventFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_fragment_event)

        viewModel = EventFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
            repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent()))

        val tabLayoutArray = arrayOf(
            "Mes events",
            "Events inscrits",
        )


        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        adapterFragment = AdapterFragmentEvent(this)
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