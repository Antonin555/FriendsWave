package com.antonin.friendswave.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentContactBinding
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ContactFragment : Fragment(), KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private lateinit var adapter1 : ListGeneriqueAdapter<User>
    private var viewModel: HomeFragmentViewModel  = HomeFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private lateinit var binding: FragmentContactBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUsers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_contact, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(HomeFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        return binding.root
    }



    override fun onResume() {
        super.onResume()
        adapter1 = ListGeneriqueAdapter(R.layout.recycler_contact)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerFragmentContact.layoutManager = layoutManager
        binding.recyclerFragmentContact.adapter = adapter1
        adapter1.addItems(contactList)
    }


    companion object {
        var contactList:ArrayList<User> = ArrayList()
    }

}