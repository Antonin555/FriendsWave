package com.antonin.friendswave.ui.event

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.ui.fragmentMain.EventFragment
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class GoogleLocation  {

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



    fun getAllLocationsEvent(googleMap:GoogleMap,viewModel: List<Event>, mapView: MapView, requireActivity: Activity,requireContext: Context) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext)

        locationManager = requireContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(requireContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(requireActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            return
        }





        // Create a LatLngBounds.Builder to calculate the bounds of all markers

        for (i in viewModel) {


//            mapView.getMapAsync { googleMap ->
//
//                val latLng = LatLng(i.lattitude.toDouble(), i.longitude.toDouble())
//                val markerOptions = MarkerOptions()
//                    .position(latLng)
//                    .title(i.name)
//                    .snippet(i.adress)
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
//                googleMap.addMarker(markerOptions)
//
//                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5f)
//                googleMap.moveCamera(cameraUpdate)
//
//            }

                val latLng1 = LatLng(i.lattitude.toDouble(), i.longitude.toDouble())
                val markerOptions = MarkerOptions()
                    .position(latLng1)
                    .title(i.name)
                    .snippet(i.adress)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                googleMap.addMarker(markerOptions)
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom( latLng, 10f))
//                 Include the marker position in the LatLngBounds.Builder
//                builder.include(latLng1)

                // Move the camera only once after all markers are added
//                if (viewModel.indexOf(i) == viewModel.size - 1) {
//                    var bounds = builder.build()
////                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds( bounds,600,200,10) // 100 is the padding in pixels
//
//                }

//            val builder = LatLngBounds.Builder()
            val latLng = LatLng(45.508888,-73.561668 )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom( latLng, 10f))

        }


    }






}