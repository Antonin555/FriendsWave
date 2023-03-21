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
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityEventsInscritsBinding

import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class EventsInscritsActivity : AppCompatActivity(), KodeinAware {

    override val kodein : Kodein by kodein()

    private val factory : EventFragmentVMFactory by instance()
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private var adapter1 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter<Event>(R.layout.recycler_events)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_inscrits)


        var binding : ActivityEventsInscritsBinding = DataBindingUtil.setContentView(this, R.layout.activity_events_inscrits)
        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)


        viewModel.fetchEventsPublic2()

        binding.viewmodel = viewModel

        val layoutManager = LinearLayoutManager(this)
        val layoutManager1 = LinearLayoutManager(this)
        binding.recyclerMyEventInscrits.layoutManager = layoutManager
        binding.recyclerMyEventInscrits.adapter = adapter1

        binding.recyclerPendingParticipants.layoutManager = layoutManager1
        binding.recyclerPendingParticipants.adapter = adapter1


        viewModel.eventList.observe(this, Observer { eventList ->
            adapter1.addItems(eventList)
        })


    }



}