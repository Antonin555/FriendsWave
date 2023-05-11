package com.antonin.friendswave.ui.event

import android.content.DialogInterface
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
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentEventsSubscribeBinding
import com.antonin.friendswave.outils.AlertDialog
import com.antonin.friendswave.ui.chat.GroupChatActivity
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
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent()))
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

        binding.recyclerPending.layoutManager = layoutManager1
        binding.recyclerPending.adapter = adapter2

        viewModel.eventListConfirm.observe(this, Observer { eventList ->
            adapter1.addItems(eventList)
        })

        viewModel.eventPendingPublic.observe(this,Observer{ eventList ->
            adapter2.addItems(eventList)
        })

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                val event = viewModel.eventListConfirm.value?.get(position)
                if(view.id == R.id.btn_delete){
                    val alert = AlertDialog(requireContext())
                    alert.showDialog(requireContext(),
                        "Suppression de votre participation",
                        "Etes vous certain de vouloir supprimer cet event ?",
                        "Confirmer",
                        "Annuler", clickOnPositiveButton(event), negativeButtonClickListener)

                }

                else {

                    val intent = Intent(view.context, GroupChatActivity::class.java)
                    intent.putExtra("eventKey", event!!.key)
                    intent.putExtra("admin", event.admin)
                    view.context.startActivity(intent)
                }



            }
        })


        adapter2.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                val event = viewModel.eventPendingPublic.value?.get(position)
                if(view.id == R.id.recycler_pending) {
                    println("JEEEEEEEEEEEEEE SUIIIIIIIISSSSS    CLLIQUEEEEEEE")
                    val bool = true
                    val intent = Intent(view.context, DetailEventActivity::class.java)
                    intent.putExtra("idEvent", event!!.key)
                    intent.putExtra("inscrit_ou_non", bool)
                    view.context.startActivity(intent)
                }

                if(view.id== R.id.btn_delete){

                    viewModel.deletePendingEvent(event)
                }


            }
        })

    }
    fun clickOnPositiveButton(event:Event?) = DialogInterface.OnClickListener { dialog, which ->

            if (which == DialogInterface.BUTTON_POSITIVE) {
                viewModel.deleteConfirmation(event)

            }
        }


    val negativeButtonClickListener = DialogInterface.OnClickListener { dialog, which ->
        // Code à exécuter si le bouton négatif est cliqué
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(requireContext(), "ok on touche a rien", Toast.LENGTH_LONG).show()
        }
    }

}


