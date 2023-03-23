package com.antonin.friendswave.ui.event

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.antonin.friendswave.ui.fragment.EventFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GoogleLocation () {


    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLastLocation : Location


    fun getLocation(requireContext: Context, mapView: MapView, requireActivity: Activity) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext)

        locationManager = requireContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(requireContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(requireActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if(location != null ) {

                mLastLocation = location
                mapView.getMapAsync { googleMap ->

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










}