package com.antonin.friendswave.ui.fragmentMain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentHomeBinding
import com.antonin.friendswave.strategy.SearchCategory
import com.antonin.friendswave.strategy.Strategy
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
    private lateinit var adapter1 : ListGeneriqueAdapter<Event>
    private lateinit var searchStrategy : Strategy

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding  = inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(HomeFragmentViewModel::class.java)
        binding.lifecycleOwner = this
        binding.item = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        adapter1 = ListGeneriqueAdapter(R.layout.recycler_events)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerFragmentHome.layoutManager = layoutManager
        binding.recyclerFragmentHome.adapter = adapter1

        viewModel.fetchUserData()

        binding.btnCategory.setOnClickListener{
            var type = "Mars"
            viewModel.fetchStrategieEvent(type)
            val searchCategory = SearchCategory()
            searchStrategy = Strategy(searchCategory)
            searchStrategy.searchByCategory()
            viewModel!!.CategorieEventList.observe(this, Observer { eventList ->
                adapter1.addItems(eventList)
            })

        }
    }

}




