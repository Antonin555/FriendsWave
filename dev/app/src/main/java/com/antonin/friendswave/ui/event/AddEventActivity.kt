package com.antonin.friendswave.ui.event





import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivityAddEventBinding
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.*
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class AddEventActivity : AppCompatActivity(), KodeinAware, OnMapReadyCallback,
    LocationListener {

    override val kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding :ActivityAddEventBinding
    private lateinit var viewModel: EventFragmentViewModel
    private var addressList: List<Address>? = null
    private lateinit var address : Address

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_event)
        viewModel = ViewModelProviders.of(this, factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.editTextTime.setIs24HourView(true)

        val apiKey = getString(R.string.api_key_google_map)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }


        binding.searchCities.setOnClickListener{

            val fields = listOf(Place.Field.ID, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        }

//        binding.addphotoevent.setOnClickListener {
//
//
//            val img = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            getResult.launch(img)
//
//
//        }



    }


//    private val getResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//
//            if (it.resultCode == Activity.RESULT_OK) {
//                img_uri = it?.data?.data!!
//                binding.imagePreviewEvent.setImageURI(img_uri)
//                viewModel.eventDataPublic.value!!.imgEvent = img_uri.toString()
//            }
//        }


    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }
    // methode de Google dans la doc:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        binding.editCities.text = place.address.toString()
                        val geoCoder = Geocoder(this)
                        try {
                            addressList = geoCoder.getFromLocationName(place.address.toString(), 1)

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        address = addressList!![0]


//                        viewModel.longitude = address.longitude.toString()
//                        viewModel.lattitude = address.latitude.toString()
                    }

                    viewModel.longitude = address.longitude.toString()
                    viewModel.lattitude = address.latitude.toString()
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }

            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}