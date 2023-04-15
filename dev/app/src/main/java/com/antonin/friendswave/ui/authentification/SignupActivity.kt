package com.antonin.friendswave.ui.authentification

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivityEditProfilBinding
import com.antonin.friendswave.databinding.ActivitySingupBinding
import com.antonin.friendswave.outils.startHomeActivity

import com.antonin.friendswave.ui.viewModel.AuthViewModel
import com.antonin.friendswave.ui.viewModel.AuthViewModelFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class SignupActivity : AppCompatActivity(), InterfaceAuth, KodeinAware {

    //    private lateinit var progressbar : ProgressBar
    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var binding: ActivitySingupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        val apiKey = getString(R.string.api_key_google_map)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

//        progressbar = findViewById(R.id.progressbar)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_singup)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.interfaceAuth = this
        viewModel.fetchEmail()
        viewModel.fetchAllPseudo()

//        viewModel.pseudoList_live.observe(this, Observer {
//            viewModel.pseudoList = it
//        })

        binding.searchCity.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME)

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setTypeFilter(
                TypeFilter.CITIES).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

    }


    override fun onSuccess() {
        startHomeActivity()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    override fun onStarted() {
////        progressbar.visibility = View.VISIBLE
//        Intent(this, HomeActivity::class.java).also {
//            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(it)
//        }
//    }
////
//    override fun onSuccess() {
////        progressbar.visibility = View.GONE
//        startHomeActivity()
//    }

    // methode de Google dans la doc:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        binding.editLieu.setText(place.name.toString())
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(ContentValues.TAG, status.statusMessage ?: "")
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