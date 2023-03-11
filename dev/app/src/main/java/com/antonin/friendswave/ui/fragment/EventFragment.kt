package com.antonin.friendswave.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.FragmentEventBinding
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class EventFragment : Fragment(), KodeinAware {


    override val kodein : Kodein by kodein()

    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding : FragmentEventBinding
    private lateinit var viewModel: EventFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        return binding.root
    }

    companion object {

    }
}