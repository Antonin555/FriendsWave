package com.antonin.friendswave.ui.event
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityMyEventManageBinding
import com.antonin.friendswave.outils.AlertDialog
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance



class MyEventManageActivity : AppCompatActivity(), KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent()))
    private var adapter1 : ListGeneriqueAdapter<User> = ListGeneriqueAdapter<User>(R.layout.recycler_contact)
    private var adapter2 : ListGeneriqueAdapter<User> = ListGeneriqueAdapter<User>(R.layout.recycler_contact)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_event_manage)
        val keyPrivate = intent.getStringExtra("clefPrivate")
        val keyPublic = intent.getStringExtra("clefPublic")
        val pos   = intent.getIntExtra("position", 0)

        var binding : ActivityMyEventManageBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_event_manage)
        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this



        if(keyPublic != null ){
            binding.clef = keyPublic
            viewModel.fetchDetailEventPublicUser(keyPublic) // event public
            viewModel.fetchGuestDetailEventPublic(keyPublic) // chercher les invitations dans l'event Public
            viewModel.fetchGuestConfirmDetailEventPublic(keyPublic)

            viewModel.keyEvent = keyPublic


        }
        if(keyPrivate != null) {
            binding.clef = keyPrivate
            viewModel.fetchEventUserPrivate(pos)
            viewModel.fetchGuestDetailEvent(keyPrivate)
            viewModel.fetchGuestAttenteEventPrive(keyPrivate)
            viewModel.keyEvent = keyPrivate
        }


        val layoutManager = LinearLayoutManager(this)
        val layoutManager1 = LinearLayoutManager(this)

        binding.recyclerMyEventInscrits.layoutManager = layoutManager
        binding.recyclerMyEventInscrits.adapter = adapter1
        binding.recyclerPendingParticipants.layoutManager = layoutManager1
        binding.recyclerPendingParticipants.adapter = adapter2


        viewModel.guestList.observe(this, Observer { guestList ->
            adapter1.addItems(guestList)
        })


        viewModel.confirm_guestListPublic.observe(this,Observer{ confirm_guestList->
            adapter1.addItems(confirm_guestList)
        })

        binding.btnDeleteMyEvent.setOnClickListener{

            val alert = AlertDialog(this)
            alert.showDialog(this,"title", "message", "OK", "Cancel", positiveButtonClickListener, negativeButtonClickListener)

        }

        viewModel.guestListPublic.observe(this,Observer{ attente_guestList->
            adapter2.addItems(attente_guestList)
        })

        viewModel.userListAttentePrivate.observe(this,Observer{ attente_guestList->
            adapter2.addItems(attente_guestList)
        })


    }

    val positiveButtonClickListener = DialogInterface.OnClickListener { dialog, which ->
        // Code à exécuter si le bouton positif est cliqué
        if (which == DialogInterface.BUTTON_POSITIVE) {

            viewModel.deleteEvent()
            finish()
        }
    }

    val negativeButtonClickListener = DialogInterface.OnClickListener { dialog, which ->
        // Code à exécuter si le bouton négatif est cliqué
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(this,"ok on touche a rien", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.fetchParticipantAttente()
    }
}