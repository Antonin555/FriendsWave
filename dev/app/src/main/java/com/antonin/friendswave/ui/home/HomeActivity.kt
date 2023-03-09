package com.antonin.friendswave.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.AdapterFragment
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeActivity : AppCompatActivity() {


    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2

    private lateinit var adapterFragment : AdapterFragment

    private lateinit var viewModel : HomeFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = HomeFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))

        val tabLayoutArray = arrayOf(
            "Home",
            "Contact",
            "Events",
            "Notifs"
        )



        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        adapterFragment = AdapterFragment(this)
        viewPager.adapter = adapterFragment
        tabLayout.getTabAt(0)!!.orCreateBadge.backgroundColor;
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager!!.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabLayoutArray[position]
        }.attach()

//        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
//        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
//        binding.viewmodel = viewModel

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController



//        button2.setOnClickListener{
//                    getLocation()
//        }



    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout){
            viewModel.logout(View(this))
            return true
        }

        if(item.itemId == R.id.contact) {
//            viewModel.addContact(View(this))
            return true
        }

        if(item.itemId == R.id.calendar) {
//            getResult.launch(viewModel.addCalendar(View(this)))
//            val intent = Intent(this@HomeActivity, Activity_Calendar::class.java)

//            getResult.launch(intent)

            return true
        }

        if(item.itemId == R.id.edit_profil) {
//            val intent = Intent(this@HomeActivity, EditProfileActivity::class.java)
            startActivity(intent)
//            val demoFragment = fragmentHobbies()
//            val transaction: FragmentTransaction = this.supportFragmentManager.beginTransaction()
//            transaction.replace(com.antonin.app_mvvm_firebase.R.id.home_layout, demoFragment)
//            transaction.addToBackStack(null).commit()
//            viewModel.fragmentHobbies(View(this))
        }

        return true
    }
}