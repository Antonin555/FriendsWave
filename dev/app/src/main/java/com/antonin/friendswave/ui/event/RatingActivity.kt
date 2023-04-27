package com.antonin.friendswave.ui.event

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.databinding.ActivityAddContactBinding
import com.antonin.friendswave.databinding.ActivityRatingBinding
import com.antonin.friendswave.ui.viewModel.ContactViewModel
import com.antonin.friendswave.ui.viewModel.ContactViewModelFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RatingActivity : AppCompatActivity(), KodeinAware {

    override val kodein : Kodein by kodein()
    private lateinit var viewModel: EventFragmentViewModel
    private val factory : EventFragmentVMFactory by instance()
    private lateinit var adapter1 : ListGeneriqueAdapter<User>
    private lateinit var binding: ActivityRatingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_rating)
        viewModel = ViewModelProviders.of(this, factory).get(EventFragmentViewModel::class.java)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onResume() {
        super.onResume()

        //fetch les participants de l'event qui ne sont pas nous
    }
}