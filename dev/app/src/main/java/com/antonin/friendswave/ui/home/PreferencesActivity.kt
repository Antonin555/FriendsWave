package com.antonin.friendswave.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivityPreferencesBinding
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import com.google.firebase.messaging.FirebaseMessaging
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PreferencesActivity : AppCompatActivity() , KodeinAware  {


    override val kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding : ActivityPreferencesBinding
    private lateinit var viewModel: EventFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_preferences)
        viewModel = ViewModelProviders.of(this, factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

    }
}