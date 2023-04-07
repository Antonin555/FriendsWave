package com.antonin.friendswave.ui.fragmentMain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentNotifsBinding
import com.antonin.friendswave.ui.viewModel.NotifFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.NotifFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class NotifsFragment : Fragment(), KodeinAware {



    override val kodein : Kodein by kodein()
    private val factory : NotifFragmentVMFactory by instance()
    private lateinit var adapter1 : ListGeneriqueAdapter<User>
    private lateinit var adapter2 : ListGeneriqueAdapter<Event>
    private lateinit var adapter3 : ListGeneriqueAdapter<User>
    private var viewModel: NotifFragmentViewModel = NotifFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private lateinit var binding : FragmentNotifsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter1 = ListGeneriqueAdapter(R.layout.recycler_requete)
        adapter2 = ListGeneriqueAdapter(R.layout.recycler_invite_events)
        adapter3 = ListGeneriqueAdapter(R.layout.recycler_demande_inscription)

//        viewModel.fetchEventsInvitation(_eventList)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_notifs, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(NotifFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        val layoutManager = LinearLayoutManager(context)
        val layoutManager2 = LinearLayoutManager(context)
        val layoutManager3 = LinearLayoutManager(context)
        binding.recyclerFragmentNotif.layoutManager = layoutManager
        binding.recyclerFragmentNotif.adapter = adapter1
        binding.recyclerFragmentNotifEvents.layoutManager = layoutManager2
        binding.recyclerFragmentNotifEvents.adapter = adapter2
        binding.recyclerRequestEvent.layoutManager = layoutManager3
        binding.recyclerRequestEvent.adapter = adapter3
        viewModel.fetchDemandeInscriptionEventPublic()


        return binding.root
    }

    override fun onResume() {
        super.onResume()



        viewModel.fetchUsersRequest()
        viewModel.friendNotifList.observe(this, Observer { notifUserList ->
            adapter1.addItems(notifUserList)
        })

        viewModel.fetchEventsInvitation()
        viewModel.eventList.observe(this,Observer { eventList ->
            adapter2.addItems(eventList)

        })


        viewModel.requestListEvent.observe(this, Observer { userList ->
            adapter3.addItems(userList)
        })

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if (view.id == R.id.btn_accept){
                    var userNotif = viewModel.friendNotifList.value?.get(position)
                    viewModel.acceptRequest(userNotif)
                }

                else if (view.id == R.id.btn_delete){
                    var userNotif = viewModel.friendNotifList.value?.get(position)
                    viewModel.refuseRequest(userNotif)
                }
            }

        })


        adapter2.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if (view.id == R.id.btn_accept){
                    val eventKey = viewModel.eventList.value?.get(position)
                    viewModel.acceptInvitationEvent(eventKey)

                }

                if (view.id == R.id.btn_delete){
                    val eventKey = viewModel.eventList.value?.get(position)
                    viewModel.refuseInvitationEvent(eventKey)

                }
            }

        })


        adapter3.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if (view.id == R.id.btn_accept){
                    val eventKey = viewModel.requestListEvent.value?.get(position)!!
                    viewModel.acceptRequestEvent(eventKey!!)

                }

                if (view.id == R.id.btn_delete){
                    val eventKey = viewModel.eventList.value?.get(position)
                    viewModel.refuseInvitationEvent(eventKey)

                }
            }

        })


    }
}