package com.antonin.friendswave.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel


class ContactFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter1 : ListGeneriqueAdapter<User>

    private var viewModel: HomeFragmentViewModel  = HomeFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchUsers()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onResume() {
        super.onResume()


        adapter1 = ListGeneriqueAdapter(R.layout.recycler_contact)

        val layoutManager = LinearLayoutManager(context)

        recyclerView = view?.findViewById(R.id.recyclerFragmentContact)!!
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter1

        adapter1.addItems(contactList)


    }



    companion object {

        var contactList:ArrayList<User> = ArrayList()

    }

}