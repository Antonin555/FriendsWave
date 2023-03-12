package com.antonin.friendswave.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentEventBinding
import com.antonin.friendswave.databinding.FragmentHomeBinding
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class EventFragment : Fragment(), KodeinAware {


    override val kodein : Kodein by kodein()

    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding : FragmentEventBinding
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))

    private lateinit var adapter1 : ListGeneriqueAdapter<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchEvents()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        return binding.root
    }


    override fun onResume() {
        super.onResume()


        adapter1 = ListGeneriqueAdapter(R.layout.recycler_events)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerFragmentEvent.layoutManager = layoutManager
        binding.recyclerFragmentEvent.adapter = adapter1
        adapter1.addItems(eventList)
    }

    companion object {
        var eventList:ArrayList<Event> = ArrayList()
    }
}