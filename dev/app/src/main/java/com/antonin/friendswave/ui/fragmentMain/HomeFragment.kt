package com.antonin.friendswave.ui.fragmentMain

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.inflate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.firebase.FirebaseStore
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentHomeBinding
import com.antonin.friendswave.ui.home.ProfilActivity
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import com.antonin.friendswave.ui.viewModel.NotifFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.NotifFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import org.kodein.di.Kodein


import org.kodein.di.KodeinAware

import org.kodein.di.generic.instance
import org.kodein.di.android.x.kodein


class HomeFragment : Fragment(), KodeinAware {

    private var storeMedia = FirebaseStore()
    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private var viewModel: HomeFragmentViewModel = HomeFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent()))
    private val factory2 : NotifFragmentVMFactory by instance()
    private var viewModel2: NotifFragmentViewModel = NotifFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent()))
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter1 : ListGeneriqueAdapter<User>
    private lateinit var adapter3 : ListGeneriqueAdapter<Event>
    private lateinit var adapter2 : ListGeneriqueAdapter<User>
    private val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1
    private var firebaseMessaging = FirebaseMessaging.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this,factory).get(HomeFragmentViewModel::class.java)
        viewModel2 = ViewModelProviders.of(this,factory2).get(NotifFragmentViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        initFCM()
        permissionNotifs() // A VOIR SI UTILE
        FirebaseMessaging.getInstance().subscribeToTopic("nom-du-topic")
        FirebaseMessaging.getInstance().subscribeToTopic("nom-du-topic1")

        binding  = inflate(inflater, R.layout.fragment_home, container, false)
        binding.item = viewModel
        binding.viewmodel = viewModel2
        binding.lifecycleOwner = this
        firebaseMessaging = FirebaseMessaging.getInstance()
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        adapter1 = ListGeneriqueAdapter(R.layout.recycler_requete)
        adapter3 = ListGeneriqueAdapter(R.layout.recycler_invite_events)
        adapter2 = ListGeneriqueAdapter(R.layout.recycler_demande_inscription)

        val layoutManager = LinearLayoutManager(context)
        val layoutManager2 = LinearLayoutManager(context)
        val layoutManager3 = LinearLayoutManager(context)

        binding.recyclerFragmentNotif.layoutManager = layoutManager
        binding.recyclerFragmentNotif.adapter = adapter1
        binding.recyclerFragmentNotifEvents.layoutManager = layoutManager2
        binding.recyclerFragmentNotifEvents.adapter = adapter2
        binding.recyclerRequestEvent.layoutManager = layoutManager3
        binding.recyclerRequestEvent.adapter = adapter3

        viewModel2.fetchUsersRequest()
        viewModel2.fetchEventsInvitation()
        viewModel2.fetchDemandeInscriptionEventPublic()
        viewModel.fetchUserData()

        viewModel2.friendNotifList.observe(this, Observer { notifUserList ->
            adapter1.addItems(notifUserList)
            if(adapter1.itemCount !=0 ) binding.makefriends.visibility= View.GONE
            else binding.makefriends.visibility = View.VISIBLE
        })

        viewModel2.eventList.observe(this,Observer { eventList ->
            adapter3.addItems(eventList)
            if( adapter3.itemCount !=0) binding.tempInvitations.visibility = View.GONE
            else binding.tempInvitations.visibility = View.VISIBLE
        })

        viewModel2.requestListEvent.observe(this, Observer { userList ->
            adapter2.addItems(userList)
            if(adapter2.itemCount != 0)  binding.searchEvents.visibility = View.GONE
            else binding.searchEvents.visibility = View.VISIBLE
        })

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
        }

        viewModel.user_live.observe(this, Observer { it ->
            val path1 = "photos/" + it.img.toString()
            val path2 = "photosCover/" + it.imgCover.toString()

            if(it.imgCover != null) storeMedia.displayImage(binding.imageCover,path2)
            storeMedia.displayImage(binding.imgProfil,path1)

        })

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                // Le token du dispositif
                val token = task.result
            }

        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val userNotif = viewModel2.friendNotifList.value?.get(position)
                if (view.id == R.id.accept_friend){
                    viewModel2.acceptRequest(userNotif)
                }

                else if (view.id == R.id.decline_friend){
                    viewModel2.refuseRequest(userNotif)
                }
                else if(view.id == R.id.imageProfil) {
                    val intent = Intent(context, ProfilActivity::class.java)
                    intent.putExtra("uid", userNotif?.uid)
                    startActivity(intent)

                }
            }

        })

        adapter2.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                val user = viewModel2.requestListEvent.value?.get(position)!!
                if (view.id == R.id.non_event){
                    viewModel2.declineRequestEvent(user)
                }
                else if(view.id == R.id.oui_event) {
                    viewModel2.acceptRequestEvent(user)
                }
                else if(view.id == R.id.profil_potential_guest) {
                    val intent = Intent(context, ProfilActivity::class.java)
                    intent.putExtra("uid", user?.uid)
                    startActivity(intent)
                }
            }

        })

        adapter3.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {

                val event = viewModel2.eventList.value?.get(position)
                if (view.id == R.id.accept_invitation){
                    event!!.nbreInscrit?.plus(1)
                    viewModel2.acceptInvitationEvent(event)
                }
                if (view.id == R.id.refuse_invitation){
                    viewModel2.refuseInvitationEvent(event)
                }
            }

        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                print("ok")
                // La permission a été accordée, vous pouvez maintenant accéder au fournisseur de documents de médias Android.
            } else {
                // La permission a été refusée, vous devez gérer le cas où l'utilisateur refuse la permission.
            }
        }
    }

    fun permissionNotifs() {
        // Declare the launcher at the top of your Activity/Fragment:
         val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                print("ok")
                // FCM SDK (and your app) can post notifications.
            } else {
                // TODO: Inform user that that your app will not show notifications.
            }
        }

    }

    private fun sendRegistrationToServer(token: String) {

        val reference = FirebaseDatabase.getInstance().reference
        reference.child("user").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("token")
            .setValue(token)
    }

    private fun initFCM() {
        val token = FirebaseInstanceId.getInstance().token
        sendRegistrationToServer(token!!)
    }

}











