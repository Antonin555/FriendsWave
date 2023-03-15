package com.antonin.friendswave.ui.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.databinding.ActivityDetailEventBinding
import com.antonin.friendswave.databinding.ActivityLoginBinding
import com.antonin.friendswave.ui.viewModel.AuthViewModel
import com.antonin.friendswave.ui.viewModel.AuthViewModelFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class DetailEventActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: EventFragmentViewModel
    private val factory : EventFragmentVMFactory by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_event)
        var pos   = intent.getIntExtra("position", 0)
        val binding: ActivityDetailEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_event)
        viewModel = ViewModelProviders.of(this, factory).get(EventFragmentViewModel::class.java)
        binding.event = viewModel
        binding.lifecycleOwner = this
        viewModel.fetchDataEvent(pos)


    }


}


