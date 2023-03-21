package com.antonin.friendswave.ui.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import com.antonin.friendswave.databinding.ActivityMesEventsBinding
import com.antonin.friendswave.ui.viewModel.EventFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.EventFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance




class MesEventsActivity : AppCompatActivity(),KodeinAware {


    override val kodein : Kodein by kodein()
    private val factory : EventFragmentVMFactory by instance()
    private var viewModel: EventFragmentViewModel = EventFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private var adapter1 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter<Event>(R.layout.recycler_events)
    private var adapter2 : ListGeneriqueAdapter<Event> = ListGeneriqueAdapter<Event>(R.layout.recycler_events)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mes_events)

        var binding : ActivityMesEventsBinding = DataBindingUtil.setContentView(this, R.layout.activity_mes_events)
        viewModel = ViewModelProviders.of(this,factory).get(EventFragmentViewModel::class.java)


        viewModel.fetchEventsPublic2()

        binding.viewmodel = viewModel

        val layoutManager = LinearLayoutManager(this)
        val layoutManager1 = LinearLayoutManager(this)
        binding.recyclerMyEventPrivate.layoutManager = layoutManager
        binding.recyclerMyEventPrivate.adapter = adapter1

        binding.recyclerMyEventPublic.layoutManager = layoutManager1
        binding.recyclerMyEventPublic.adapter = adapter2

        // a mettre aussi pour les events privees ou public :

        viewModel.eventList.observe(this, Observer { eventList ->
            adapter1.addItems(eventList)
        })

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                val toast = Toast.makeText(applicationContext, "Hello Javatpoint" + position.toString(), Toast.LENGTH_SHORT)
                toast.show()

                var intent : Intent = Intent(applicationContext,MyEventManageActivity::class.java)
                startActivity(intent)
//                var intent : Intent = Intent(applicationContext, DetailEventActivity::class.java )
//                intent.putExtra("position", position)
//                startActivity(intent)
            }
        })

        adapter2.setOnListItemViewClickListener(object:ListGeneriqueAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                var intent : Intent = Intent(applicationContext,MyEventManageActivity::class.java)
                startActivity(intent)
            }


        })

    }

}