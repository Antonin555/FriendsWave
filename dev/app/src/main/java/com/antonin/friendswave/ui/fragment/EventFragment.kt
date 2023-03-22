package com.antonin.friendswave.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance



class EventFragment : Fragment(), KodeinAware, InterfaceEvent, OnMapReadyCallback,
    LocationListener {


    override val kodein : Kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding : FragmentEventBinding
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private var adapter1 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter<Event>(R.layout.recycler_events)

    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLastLocation : Location

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
        binding.mapView.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocation()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()

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


    companion object {
        lateinit var mMap: GoogleMap
    }



    private fun getLocation() {

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if(location != null ) {

                mLastLocation = location
                binding.mapView.getMapAsync { googleMap ->

                    EventFragment.mMap = googleMap

                    val latLng = LatLng(location.latitude, location.longitude)
                    val markerOptions = MarkerOptions()
                        .position(latLng)
                        .title("My Marker")
                        .snippet("This is my marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    googleMap.addMarker(markerOptions)

                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
                    googleMap.moveCamera(cameraUpdate)



                }

            }
        }

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