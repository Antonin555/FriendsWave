package com.antonin.friendswave.ui.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseStore
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.databinding.ActivityDetailEventBinding
import com.antonin.friendswave.outils.AnimationLayout
import com.antonin.friendswave.ui.home.ProfilActivity
import com.antonin.friendswave.ui.viewModel.ContactViewModel
import com.antonin.friendswave.ui.viewModel.ContactViewModelFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class DetailEventActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: EventFragmentViewModel
    private val factory : EventFragmentVMFactory by instance()
    val storeMedia = FirebaseStore()
    private var adapter1 : ListGeneriqueAdapter<User> = ListGeneriqueAdapter<User>(R.layout.recycler_contact)
    private var bool_linear_inscrit = true
    private var bool_linear_description_event = true
    private val animeLayout = AnimationLayout()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_event)

        val bool   = intent.getBooleanExtra("inscrit_ou_non", false)
        val idEvent = intent.getStringExtra("idEvent")
        val adminEvent = intent.getStringExtra("adminEvent")

        val binding: ActivityDetailEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_event)
        viewModel = ViewModelProviders.of(this, factory).get(EventFragmentViewModel::class.java)

        binding.event = viewModel
        binding.idEvent = idEvent
        binding.adminEvent = adminEvent

        binding.lifecycleOwner = this

        val layoutManager = LinearLayoutManager(this)

        binding.recyclerConfirmGuest.layoutManager = layoutManager
        binding.recyclerConfirmGuest.adapter = adapter1

        if(bool) {
            binding.btnInscription.visibility = View.INVISIBLE
        }

        viewModel.fetchDataEvent(idEvent.toString())
        viewModel.fetchGuestConfirmDetailEventPublic(idEvent)
        viewModel.fetchEmail()
        viewModel.fetchAdmin(adminEvent.toString())
        viewModel.fetchUserData()

        viewModel.admin_live.observe(this, Observer {
            binding.profilHost.text = it.name
        })

        viewModel.confirm_guestListPublic.observe(this, Observer{ confirm_guestList->
            adapter1.addItems(confirm_guestList)
        })




        viewModel.eventData.observe(this, Observer { it ->
            val path1 = "photosEvent/" + it.imgEvent.toString()
            storeMedia.displayImage(binding.imagePreviewEvent, path1 )
        })

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                val idGuest = viewModel.confirm_guestListPublic.value!!.get(position).uid
                val intent = Intent(view.context, ProfilActivity::class.java )
                intent.putExtra("uid", idGuest)
                startActivity(intent)

            }

        })

        binding.linearDescriptionEvent.setOnClickListener{

            if(bool_linear_description_event == true){

                animeLayout.expand(binding.linearDescriptionEvent, 1000,500)
                bool_linear_description_event = false

            } else {

                animeLayout.collapse(binding.linearDescriptionEvent, 1000, 40)
                bool_linear_description_event = true
            }

        }

        binding.linearInscritEvent.setOnClickListener{


            if(bool_linear_inscrit == true) {

                animeLayout.expand(binding.linearInscritEvent, 1000, 500)
                bool_linear_inscrit = false
            }else {

                animeLayout.collapse(binding.linearInscritEvent, 1000, 40)
                bool_linear_inscrit = true
            }
        }

        binding.profilHost.setOnClickListener{

            val intent = Intent(this, ProfilActivity::class.java )
            intent.putExtra("uid", adminEvent)
            startActivity(intent)
        }



    }






}


