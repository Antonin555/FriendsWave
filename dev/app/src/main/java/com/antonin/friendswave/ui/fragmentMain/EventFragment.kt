package com.antonin.friendswave.ui.fragmentMain

import android.annotation.SuppressLint
import android.content.Intent

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.EventRepo
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


class EventFragment : Fragment(), KodeinAware, InterfaceEvent, OnMapReadyCallback, LocationListener {


    override val kodein : Kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding : FragmentEventBinding
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent()))
    private var adapter1 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter<Event>(R.layout.recycler_events)
    private lateinit var loc : GoogleLocation
    private lateinit var googleMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loc = GoogleLocation()

        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)
        viewModel.interfaceEvent = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
        binding.viewmodel = viewModel


        binding.mapView.getMapAsync(this)
        binding.mapView.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        return binding.root


    }


    @SuppressLint("ResourceType")
    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()

        viewModel.fetchEventsPublic1()
        viewModel.fetchUserData()

        viewModel.eventList.observe(this, Observer { eventList ->
            adapter1.addItems(eventList)
//
        })

//        var tempList : ArrayList<Event> = ArrayList()
        val searchCategory = SearchCategory()
        val searchByCities = SearchByCities()
        val searchByName = SearchByName()
//        var searchStrategy : Strategy

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerFragmentEvent.layoutManager = layoutManager
        binding.recyclerFragmentEvent.adapter = adapter1

        binding.btnRecherche.setOnClickListener{
            var searchStrategy = Strategy(searchCategory)
            val type = viewModel.strCategory.value.toString()

            if(viewModel.categorie == "(km) Autour de toi"){
                if(!type.isDigitsOnly()){
                    Toast.makeText(context, "Vous devez inscrire un nombre", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                searchStrategy = Strategy(searchByCities)
            }
            //A CHANGER IL N'Y A PAS D'INTERET DANS LES EVENTS, METTRE DATE?
            else if(viewModel.categorie == "Nom"){
                searchStrategy = Strategy(searchByName)
            }
            else if(viewModel.categorie == "Categorie"){
                searchStrategy = Strategy(searchCategory)
            }


            viewModel.strCategory.value = ""
            strategyEvent(searchStrategy,type)
        }

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val id_event = viewModel.eventList.value!!.get(position).key
                val admin_event = viewModel.eventList.value!!.get(position).admin
                goToDetailEvent(id_event,admin_event, position)
            }
        })
    }


    fun strategyEvent(strategy: Strategy, str:String) {
        var tempList: ArrayList<Event>
        viewModel.eventList.observe(this, Observer { eventList ->
            val user = viewModel.user_live.value
            tempList = strategy.search(str, eventList, user!!) as ArrayList<Event>
            adapter1.addItems(tempList)

            adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
                override fun onClick(view: View, position: Int) {
                    val id_event = tempList.get(position).key
                    val admin_event = tempList.get(position).admin
                    goToDetailEvent(id_event,admin_event,position)

                }
            })
        })

    }

    fun goToDetailEvent(id_event:String?, admin_event:String , position:Int){

        val intent = Intent(context, DetailEventActivity::class.java )
        intent.putExtra("position", position)
        intent.putExtra("idEvent", id_event)
        intent.putExtra("adminEvent", admin_event)
        startActivity(intent)

    }

    override fun saveOn() {
        TODO("Not yet implemented")
    }

    override fun saveOff() {
        TODO("Not yet implemented")
    }

    override fun checkContent() {

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

        googleMap = p0
//        loc.getLocation(requireContext(),binding.mapView,requireActivity())
//        googleMap.addMarker(MarkerOptions().position(LatLng(-73.4220, 45.0841)).title("Marker à Mountain View"))
        viewModel.eventList.observe(requireActivity(), Observer { eventList ->

            loc.getAllLocationsEvent(googleMap,eventList,binding.mapView, requireActivity(),requireContext())
        })
//
    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }
}
