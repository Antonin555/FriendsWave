package com.antonin.friendswave.ui.event

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivityAddEventBinding
import com.antonin.friendswave.databinding.ActivitySingupBinding
import com.antonin.friendswave.ui.viewModel.AuthViewModel
import com.antonin.friendswave.ui.viewModel.AuthViewModelFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AddEventActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    private val factory : EventFragmentVMFactory by instance()

    private lateinit var viewModel: EventFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)


        val binding: ActivityAddEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_event)
        viewModel = ViewModelProviders.of(this, factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewModel
    }
}