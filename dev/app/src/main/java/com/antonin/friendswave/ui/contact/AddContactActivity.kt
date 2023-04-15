package com.antonin.friendswave.ui.contact


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.databinding.ActivityAddContactBinding
import com.antonin.friendswave.strategy.*
import com.antonin.friendswave.ui.viewModel.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class AddContactActivity : AppCompatActivity(), KodeinAware {

    override val kodein : Kodein by kodein()
    private lateinit var viewModel: ContactViewModel
    private val factory : ContactViewModelFactory by instance()
    private lateinit var adapter1 : ListGeneriqueAdapter<User>
    private lateinit var binding: ActivityAddContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_contact)
        viewModel = ViewModelProviders.of(this, factory).get(ContactViewModel::class.java)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onResume() {
        super.onResume()
        //pour verifier si la personne est dans l'app
        viewModel.fetchEmail()
        //pour les strategies de referencement
        viewModel.fetchAllUser()
        viewModel.fetchUserData()

        adapter1 = ListGeneriqueAdapter(R.layout.recycler_contact)
        val layoutManager2 = LinearLayoutManager(this)
        binding.recyclerSuggestion.layoutManager = layoutManager2
        binding.recyclerSuggestion.adapter = adapter1

        val searchHobbyFriend = SearchHobbyFriend()
        val searchCityFriend = SearchCityFriend()
        val searchAgeFriend = SearchAgeFriend()
        var searchStrategyFriend: StrategyFriend


        binding.btnHobby.setOnClickListener{
            searchStrategyFriend = StrategyFriend(searchHobbyFriend)
            strategyUser(searchStrategyFriend)
        }
        binding.btnCity.setOnClickListener{
            searchStrategyFriend = StrategyFriend(searchCityFriend)
            strategyUser(searchStrategyFriend)
        }
        binding.btnAge.setOnClickListener{
            searchStrategyFriend = StrategyFriend(searchAgeFriend)
            strategyUser(searchStrategyFriend)
        }

    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
////        return inflater.inflate(R.layout.fragment_contact, container, false)
//
//        // Inflate the layout for this fragment
//        binding  = DataBindingUtil.inflate(inflater, R.layout.activity_add_contact, container, false)
//        viewModel = ViewModelProviders.of(this,factory).get(ContactViewModel::class.java)
//        binding.viewmodel = viewModel
//        return binding.root
//    }

    fun strategyUser(strategy: StrategyFriend) {

        var tempList : ArrayList<User> =  ArrayList()
        viewModel.totalUserList.observe(this, Observer { userList ->
            tempList = strategy.search(viewModel.user_live.value, userList) as ArrayList<User>
            adapter1.addItems(tempList)
        })

    }


}