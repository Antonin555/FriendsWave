package com.antonin.friendswave.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.data.model.User
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
//    private lateinit var progressbar : ProgressBar


    private lateinit var viewModel: HomeFragmentViewModel

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding  = inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(HomeFragmentViewModel::class.java)
        binding.item = viewModel
        binding.userName = viewModel.userName.toString()
        str = binding.userName
        return binding.root
    }




    companion object {

        var str : String? = ""
    }




}




