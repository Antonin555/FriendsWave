package com.antonin.friendswave.ui.fragmentMain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentHomeBinding
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import org.kodein.di.Kodein

import org.kodein.di.KodeinAware

import org.kodein.di.generic.instance
import org.kodein.di.android.x.kodein

class HomeFragment : Fragment(), KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private var viewModel: HomeFragmentViewModel = HomeFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private lateinit var binding: FragmentHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this,factory).get(HomeFragmentViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding  = inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.item = viewModel
    }

}



