package com.antonin.friendswave.ui.event

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityDetailEventBinding
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class DetailEventActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: EventFragmentViewModel
    private val factory : EventFragmentVMFactory by instance()

    private var adapter1 : ListGeneriqueAdapter<User> = ListGeneriqueAdapter<User>(R.layout.recycler_contact)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_event)

        val pos   = intent.getIntExtra("position", 0)
        val idEvent = intent.getStringExtra("idEvent")
        val adminEvent = intent.getStringExtra("adminEvent")

        val binding: ActivityDetailEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_event)
        viewModel = ViewModelProviders.of(this, factory).get(EventFragmentViewModel::class.java)


        binding.event = viewModel
        binding.idEvent = idEvent
        binding.adminEvent = adminEvent

        binding.lifecycleOwner = this

        val layoutManager = LinearLayoutManager(this)

        binding.recyclerConfirmGuest.layoutManager = layoutManager
        binding.recyclerConfirmGuest.adapter = adapter1
        viewModel.fetchDataEvent(pos)
        viewModel.fetchGuestConfirmDetailEventPublic(idEvent)


        viewModel.confirm_guestListPublic.observe(this, Observer{ confirm_guestList->
            adapter1.addItems(confirm_guestList)
        })
    }


}


