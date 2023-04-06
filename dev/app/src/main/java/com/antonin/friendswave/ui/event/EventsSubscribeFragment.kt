package com.antonin.friendswave.ui.event

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentEventsSubscribeBinding

import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class EventsSubscribeFragment : Fragment(), KodeinAware{

    override val kodein : Kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private lateinit var binding : FragmentEventsSubscribeBinding
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private var adapter1 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter(R.layout.recycler_my_event_inscrits)
    private var adapter2 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter(R.layout.recycler_my_event_pending_participants)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)
        viewModel.fetchConfirmationEvents()
        viewModel.getAllEventsPendingRequestPublic()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_events_subscribe, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onResume() {
        super.onResume()

        val layoutManager = LinearLayoutManager(context)
        val layoutManager1 = LinearLayoutManager(context)
        binding.recyclerMyEventInscrits.layoutManager = layoutManager
        binding.recyclerMyEventInscrits.adapter = adapter1

        binding.recyclerPendingParticipants.layoutManager = layoutManager1
        binding.recyclerPendingParticipants.adapter = adapter2

        viewModel.eventListConfirm.observe(this, Observer { eventList ->
            adapter1.addItems(eventList)
        })

        viewModel.eventPendingPublic.observe(this,Observer{ eventList ->
            adapter2.addItems(eventList)
        })

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                val toast = Toast.makeText(context, "Hello Javatpoint" + position.toString(), Toast.LENGTH_SHORT)
                toast.show()

//                val intent = Intent(context,ChatGroupActivity::class.java)
//
//                intent.putExtra("position", position)
//                startActivity(intent)

            }
        })

    }

}