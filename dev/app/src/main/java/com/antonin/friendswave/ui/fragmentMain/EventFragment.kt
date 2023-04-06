package com.antonin.friendswave.ui.fragmentMain

import android.content.Intent
import android.location.Location
import android.location.LocationListener
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
import com.antonin.friendswave.strategy.SearchByCities
import com.antonin.friendswave.strategy.SearchByName
import com.antonin.friendswave.strategy.SearchCategory
import com.antonin.friendswave.strategy.Strategy
import com.antonin.friendswave.ui.event.DetailEventActivity
import com.antonin.friendswave.ui.event.GoogleLocation
import com.antonin.friendswave.ui.event.InterfaceEvent
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import com.google.android.gms.maps.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance



class EventFragment : Fragment(), KodeinAware, InterfaceEvent, OnMapReadyCallback,
    LocationListener{


    override val kodein : Kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding : FragmentEventBinding
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private var adapter1 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter<Event>(R.layout.recycler_events)
    private lateinit var loc : GoogleLocation



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)
        viewModel.fetchEventsPublic1()
        viewModel.interfaceEvent = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.mapView.onCreate(savedInstanceState)

        loc = GoogleLocation()
        loc.getLocation(requireContext(),binding.mapView,requireActivity())
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        viewModel.eventList.observe(this, Observer { eventList ->
            adapter1.addItems(eventList)
        })


        var tempList : ArrayList<Event> = ArrayList()
        val searchCategory = SearchCategory()
        val searchByCities = SearchByCities()
        val searchByName = SearchByName()
        var searchStrategy : Strategy


        val layoutManager = LinearLayoutManager(context)
        binding.recyclerFragmentEvent.layoutManager = layoutManager
        binding.recyclerFragmentEvent.adapter = adapter1


        binding.btnCat.setOnClickListener{

            var type = "Mars"
            searchStrategy = Strategy(searchCategory)
            strategyEvent(searchStrategy,type)

        }

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val idEvent = viewModel.eventList.value!!.get(position).key
                val adminEvent = viewModel.eventList.value!!.get(position).admin
                println("PLEEEEEEEEEEEEEEEEEEEEASSSSSSSSSSSSSE : "+ idEvent)
                val toast = Toast.makeText(context, "Hello Javatpoint" + position.toString(), Toast.LENGTH_SHORT)
                toast.show()
                val intent = Intent(context, DetailEventActivity::class.java )
                intent.putExtra("position", position)
                intent.putExtra("idEvent", idEvent)
                intent.putExtra("adminEvent", adminEvent)
                startActivity(intent)
            }
        })


    }

    fun strategyEvent(strategy: Strategy, str:String) {
        var tempList : ArrayList<Event> =  ArrayList()
        viewModel.eventList.observe(this, Observer { eventList ->
            tempList = strategy.search(str, eventList) as ArrayList<Event>
            adapter1.addItems(tempList)
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


    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")

    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }
}