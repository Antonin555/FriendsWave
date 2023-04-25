package com.antonin.friendswave.ui.fragmentMain

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil.inflate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseNotificationsService
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentHomeBinding
import com.antonin.friendswave.databinding.FragmentNotifsBinding
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import com.antonin.friendswave.ui.viewModel.NotifFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.NotifFragmentViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.kodein.di.Kodein


import org.kodein.di.KodeinAware

import org.kodein.di.generic.instance
import org.kodein.di.android.x.kodein
import java.net.URL

class HomeFragment : Fragment(), KodeinAware {




    var storage: FirebaseStorage = Firebase.storage

    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private var viewModel: HomeFragmentViewModel = HomeFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent()))
    private val factory2 : NotifFragmentVMFactory by instance()
    private var viewModel2: NotifFragmentViewModel = NotifFragmentViewModel(repository = UserRepo(firebaseUser = FirebaseSourceUser()),
    repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent()))
    private lateinit var binding: FragmentHomeBinding
    private lateinit var binding2 : FragmentNotifsBinding

    private lateinit var adapter1 : ListGeneriqueAdapter<User>
    private lateinit var adapter2 : ListGeneriqueAdapter<Event>
    private lateinit var adapter3 : ListGeneriqueAdapter<User>

    private val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1
    private var firebaseMessaging = FirebaseMessaging.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter1 = ListGeneriqueAdapter(R.layout.recycler_requete)
        adapter2 = ListGeneriqueAdapter(R.layout.recycler_invite_events)
        adapter3 = ListGeneriqueAdapter(R.layout.recycler_demande_inscription)
        initFCM()

//        storageRef.downloadUrl.addOnSuccessListener {
//            Glide.with(binding.imgProfil.context)
//                .load(it)
//                .apply(RequestOptions().override(100, 100))
//                .centerCrop()
//                .into(binding.imgProfil)
//        }.addOnFailureListener {
//            println(it)
//        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {


        permissionNotifs()
        FirebaseMessaging.getInstance().subscribeToTopic("nom-du-topic")
        FirebaseMessaging.getInstance().subscribeToTopic("nom-du-topic1")

        binding  = inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(HomeFragmentViewModel::class.java)
        binding.lifecycleOwner = this
        binding.item = viewModel


        binding2  = inflate(inflater, R.layout.fragment_notifs, container, false)
        viewModel2 = ViewModelProviders.of(this,factory2).get(NotifFragmentViewModel::class.java)
        binding.viewmodel = viewModel2
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

        firebaseMessaging = FirebaseMessaging.getInstance()

        viewModel2.fetchUsersRequest()
        viewModel2.fetchEventsInvitation()
        viewModel2.fetchDemandeInscriptionEventPublic()

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserData()

        viewModel2.friendNotifList.observe(this, Observer { notifUserList ->
            adapter1.addItems(notifUserList)
            if(adapter1.itemCount !=0 ) binding.makefriends.visibility= View.GONE

        })

        viewModel2.eventList.observe(this,Observer { eventList ->
            adapter2.addItems(eventList)
            if( adapter2.itemCount !=0) binding.searchEvents.visibility = View.GONE
        })

        viewModel2.requestListEvent.observe(this, Observer { userList ->
            adapter3.addItems(userList)
            if( adapter3.itemCount != 0){binding.tempInvitations.visibility =View.GONE}
        })


//        adapter1 = ListGeneriqueAdapter(R.layout.recycler_events)
//        val layoutManager = LinearLayoutManager(context)
//        binding.recyclerFragmentHome.layoutManager = layoutManager
//        binding.recyclerFragmentHome.adapter = adapter1
//
//        viewModel.fetchStrategieEvent()
//        viewModel.fetchUserData()


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
        }

        viewModel.user_live.observe(this, Observer { it ->
            var storageRef = storage.reference.child("photos/" + it.img.toString())

            storageRef.downloadUrl.addOnSuccessListener {
                Glide.with(binding.imgProfil.context)
                    .load(it)
                    .apply(RequestOptions().override(100, 100))
                    .centerCrop()
                    .into(binding.imgProfil)
            }.addOnFailureListener {
                println(it)
            }

        })





//        binding.btnCategory.setOnClickListener{
//            var type = "Mars"
//            searchStrategy = Strategy(searchCategory)
//            strategyEvent(searchStrategy,type)
//
//            binding.chatlogo.visibility = View.GONE
//
//
//        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Échec de la récupération du token du dispositif.", task.exception)
                    return@addOnCompleteListener
                }

                // Le token du dispositif
                val token = task.result
                Log.d(TAG, "Token du dispositif : $token")
            }



        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if (view.id == R.id.btn_accept){
                    var userNotif = viewModel2.friendNotifList.value?.get(position)
                    viewModel2.acceptRequest(userNotif)
                }

                else if (view.id == R.id.btn_delete){
                    var userNotif = viewModel2.friendNotifList.value?.get(position)
                    viewModel2.refuseRequest(userNotif)
                }
            }

        })


        adapter2.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if (view.id == R.id.btn_accept){
                    val event = viewModel2.eventList.value?.get(position)
                    viewModel2.acceptInvitationEvent(event)

                }

                if (view.id == R.id.btn_delete){
                    val eventKey = viewModel2.eventList.value?.get(position)
                    viewModel2.refuseInvitationEvent(eventKey)

                }
            }

        })

        adapter3.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if (view.id == R.id.btn_accept){
                    val eventKey = viewModel2.requestListEvent.value?.get(position)!!
                    viewModel2.acceptRequestEvent(eventKey!!)

                }

                if (view.id == R.id.btn_delete){
                    val eventKey = viewModel2.requestListEvent.value?.get(position)
                    viewModel2.declineRequestEvent(eventKey)

                }
            }

        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                // FCM SDK (and your app) can post notifications.
            } else {
                // TODO: Inform user that that your app will not show notifications.
            }
        }

        fun askNotificationPermission() {
            // This is only necessary for API level >= 33 (TIRAMISU)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    // FCM SDK (and your app) can post notifications.
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // TODO: display an educational UI explaining to the user the features that will be enabled
                    //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                    //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                    //       If the user selects "No thanks," allow the user to continue without notifications.
                } else {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
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











