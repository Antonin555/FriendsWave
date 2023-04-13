package com.antonin.friendswave.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityProfilBinding
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class ProfilActivity : AppCompatActivity(), KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private var viewModel: HomeFragmentViewModel = HomeFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent())
    )
    private lateinit var binding: ActivityProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val profilUid = intent.getStringExtra("uid")
        binding= DataBindingUtil.setContentView(this, R.layout.activity_profil)
        viewModel = ViewModelProviders.of(this, factory).get(HomeFragmentViewModel::class.java)

        binding.item = viewModel
        binding.lifecycleOwner = this
        binding.item?.profilUid = profilUid
        //a changer puisque le UId est dispo dasn le viewModel
//        binding.item?.fetchUserProfilData(profilUid)
        viewModel.fetchUserProfilData(profilUid)
        binding.item?.verifAmitier(profilUid)
        viewModel.fetchUserData()



        viewModel.ami_live.observe(this, Observer { message ->
            if(viewModel.ami_live?.value == true){


                val addIcon = resources.getDrawable(android.R.drawable.ic_delete)
                binding.floatingActionButton.setImageDrawable(addIcon)
            }
            if(viewModel.ami_live?.value == false){

                val addIcon = resources.getDrawable(android.R.drawable.ic_input_add)
                binding.floatingActionButton.setImageDrawable(addIcon)
            }
        })


        viewModel.user_liveProfil.observe(this, Observer { it ->
            Glide.with(binding.imgProfil.context)
                .load(it.img)
                .apply(RequestOptions().override(100, 100))
                .centerCrop()
                .into(binding.imgProfil)
        })

    }
// Essai pour recup photo , ne fonctionne pas dans le onCreate ou dans le onResume
//    override fun onResume() {
//        super.onResume()
//        viewModel.user_liveProfil.observe(this, Observer { it ->
//            Glide.with(binding.imgProfil.context)
//                .load(it.img)
//                .apply(RequestOptions().override(100, 100))
//                .centerCrop()
//                .into(binding.imgProfil)
//        })
//
//    }


}