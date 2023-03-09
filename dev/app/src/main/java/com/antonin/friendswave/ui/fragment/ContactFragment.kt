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

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter1 = ListGeneriqueAdapter(R.layout.recycler_contact)

        val layoutManager = LinearLayoutManager(context)

        recyclerView = view?.findViewById(R.id.recyclerFragmentContact)!!

        recyclerView.layoutManager = layoutManager

//        adapter = HomeAdapter(requireContext(), eventList)
//        adapter = ItemsAdapter(eventList)
        recyclerView.adapter = adapter1

        viewModel.fetchUser()
        adapter1.addItems(contactList)

        println("hello")




    }

    companion object {

        var contactList:ArrayList<User> = ArrayList()

    }

}