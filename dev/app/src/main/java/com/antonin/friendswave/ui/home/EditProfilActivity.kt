package com.antonin.friendswave.ui.home


import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.MyGridViewAdapter
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityEditProfilBinding
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class EditProfilActivity : AppCompatActivity(), KodeinAware {


    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private var viewModel: HomeFragmentViewModel = HomeFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent())
    )
    private lateinit var binding: ActivityEditProfilBinding
    private lateinit var adapter: MyGridViewAdapter
    private lateinit var img_uri : Uri
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profil)
        viewModel = ViewModelProviders.of(this, factory).get(HomeFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this


        val apiKey = getString(R.string.api_key_google_map)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        viewModel.fetchUserData()
        viewModel.fetchInteret()
        actualiserSpinner()
        afficheInteret()

        viewModel.user_live.observe(this, Observer {

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val date = LocalDate.parse(it.date, formatter)
            viewModel.day = date.dayOfMonth
            viewModel.month = date.monthValue
            viewModel.year = date.year
        })

        binding.btnLoad.setOnClickListener {
            val img = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getResult.launch(img)

        }

        binding.searchCity.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME)

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setTypeFilter(
                TypeFilter.CITIES).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == Activity.RESULT_OK) {

                img_uri = it?.data?.data!! as Uri
                binding.imgPreview.setImageURI(img_uri)
                viewModel.registerPhoto(img_uri, this)

            }
        }

    fun actualiserSpinner(){
        var positionToSelect = 0
        val mySpinnerL = binding.spinnerLangues
        val mySpinnerE = binding.spinnerEtudes
        //pour inserer les donnees actuelles de l'user dans les spinners
        binding.viewmodel!!.user_live.observe(this, Observer {
            for (i in 0 until mySpinnerL.count) {
                if (mySpinnerL.getItemAtPosition(i).toString() == it?.langue) {
                    positionToSelect = i
                    break
                }
            }
            mySpinnerL.setSelection(positionToSelect)
            positionToSelect = 0
            for (i in 0 until mySpinnerE.count) {
                if (mySpinnerE.getItemAtPosition(i).toString() == it?.etude) {
                    positionToSelect = i
                    break
                }
            }
            mySpinnerE.setSelection(positionToSelect)
        })
    }

    fun afficheInteret(){
        binding.viewmodel!!.interetList.observe(this, Observer{
            adapter = MyGridViewAdapter(this, it, viewModel!!)
            binding.gridView.adapter = adapter
        })

    }

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





