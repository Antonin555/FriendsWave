package com.antonin.friendswave.ui.event

import android.app.Activity
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivityAddEventBinding
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.database.core.Context
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AddEventActivity : AppCompatActivity(), KodeinAware, OnMapReadyCallback,
    LocationListener {

    override val kodein by kodein()

    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding :ActivityAddEventBinding

    private lateinit var viewModel: EventFragmentViewModel
    private lateinit var loc: GoogleLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)


        val binding: ActivityAddEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_event)
        viewModel = ViewModelProviders.of(this, factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.editTextTime.setIs24HourView(true)

        loc = GoogleLocation()
        loc.getLocation(applicationContext, binding.mapView2, requireActivity = Activity())
    }

    override fun onPause() {
        super.onPause()
        binding.mapView2.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView2.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView2.onLowMemory()
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")

    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }
}