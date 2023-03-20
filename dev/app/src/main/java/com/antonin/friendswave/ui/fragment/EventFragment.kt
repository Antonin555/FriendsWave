package com.antonin.friendswave.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentEventBinding
import com.antonin.friendswave.databinding.FragmentHomeBinding
import com.antonin.friendswave.ui.event.DetailEventActivity
import com.antonin.friendswave.ui.event.InterfaceEvent
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance



class EventFragment : Fragment(), KodeinAware, InterfaceEvent {

    var eventList1:ArrayList<Event> = ArrayList()

    override val kodein : Kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding : FragmentEventBinding
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private var adapter1 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter<Event>(R.layout.recycler_events)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)
        viewModel.fetchEventsPublic1()

        viewModel.interfaceEvent = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    override fun onResume() {
        super.onResume()


        viewModel.eventList.observe(this, Observer { eventList ->
            adapter1.addItems(eventList)
        })

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerFragmentEvent.layoutManager = layoutManager
        binding.recyclerFragmentEvent.adapter = adapter1

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                val toast = Toast.makeText(context, "Hello Javatpoint" + position.toString(), Toast.LENGTH_SHORT)
                toast.show()
                var intent : Intent = Intent(context, DetailEventActivity::class.java )
                intent.putExtra("position", position)
                startActivity(intent)
            }
        })


    }



    override fun saveOn() {
        TODO("Not yet implemented")
    }

    override fun saveOff() {
        TODO("Not yet implemented")
    }

    override fun checkContent() {
        println("checkkkkkkkkkkkkkkkkkkkk innnnnnnnnnnnnnnnnnnnnnnntent")



    }
}