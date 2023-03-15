package com.antonin.friendswave.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentNotifsBinding
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import com.antonin.friendswave.ui.viewModel.NotifFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.NotifFragmentViewModel
import com.google.android.material.button.MaterialButton
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class NotifsFragment : Fragment(), KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : NotifFragmentVMFactory by instance()
    private lateinit var adapter1 : ListGeneriqueAdapter<User>
    private var viewModel: NotifFragmentViewModel = NotifFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private lateinit var binding : FragmentNotifsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUsersR()
        viewModel.fetchUsersRequest()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_notifs, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(NotifFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        adapter1 = ListGeneriqueAdapter(R.layout.recycler_requete)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerFragmentNotif.layoutManager = layoutManager
        binding.recyclerFragmentNotif.adapter = adapter1
        adapter1.addItems(requestList)


        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                if(view.id == R.id.btn_accept) {

                    viewModel.acceptRequest(position)
                    val toast = Toast.makeText(context, "Hello Javatpoint" + position.toString(), Toast.LENGTH_SHORT)
                    toast.show()

                }

                else if (view.id == R.id.btn_refuse){
                    viewModel.refuseRequest(position)
                    val toast = Toast.makeText(context, "Hello Javatpoint" + position.toString(), Toast.LENGTH_SHORT)
                    toast.show()

                }

//                if(view.findViewById<Button>(R.id.btn_accept).text == "ACCEPT") {
//
//                }
//                if(view.findViewById<Button>(R.id.btn_refuse).text == "DECLINE"){
//
//                }
            }

        })

    }

    companion object {
        var requestList:ArrayList<User> = ArrayList()
        var user: User? = User()
    }

}