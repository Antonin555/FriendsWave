package com.antonin.friendswave.ui.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.antonin.friendswave.R
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityEditProfilBinding
import com.antonin.friendswave.databinding.FragmentHomeBinding
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EditProfilActivity : AppCompatActivity(), KodeinAware {


    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private var viewModel: HomeFragmentViewModel = HomeFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private lateinit var binding: ActivityEditProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)



    }


}





