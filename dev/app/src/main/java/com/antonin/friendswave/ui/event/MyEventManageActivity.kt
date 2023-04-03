package com.antonin.friendswave.ui.event
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityMyEventManageBinding
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance



class MyEventManageActivity : AppCompatActivity(), KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
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
        binding.position = pos


        if(keyPublic != null ){
            viewModel.fetchDetailEventPublicUser(keyPublic)
            viewModel.fetchGuestDetailEventPublic(keyPublic)

        }
        if(keyPrivate != null) {
            viewModel.fetchEventUser(pos)
            viewModel.fetchGuestDetailEvent(keyPrivate)
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

        viewModel.guestListPublic.observe(this,Observer{ guestList ->
            adapter2.addItems(guestList)
        })



    }
}