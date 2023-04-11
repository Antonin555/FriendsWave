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
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.databinding.DataBindingUtil.inflate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.data.firebase.FirebaseNotificationsService
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.FragmentHomeBinding
import com.antonin.friendswave.outils.AlertDialog
import com.antonin.friendswave.strategy.SearchByCities
import com.antonin.friendswave.strategy.SearchByName
import com.antonin.friendswave.strategy.SearchCategory
import com.antonin.friendswave.strategy.Strategy
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.kodein.di.Kodein

import org.kodein.di.KodeinAware

import org.kodein.di.generic.instance
import org.kodein.di.android.x.kodein

class HomeFragment : Fragment(), KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private var viewModel: HomeFragmentViewModel = HomeFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter1 : ListGeneriqueAdapter<Event>
    private val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1
    private lateinit var firebaseMessaging: FirebaseMessaging

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        permissionNotifs()
        binding  = inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProviders.of(this,factory).get(HomeFragmentViewModel::class.java)
        binding.lifecycleOwner = this
        binding.item = viewModel
        firebaseMessaging = FirebaseMessaging.getInstance()

        return binding.root

    }

    override fun onResume() {
        super.onResume()

        FirebaseMessaging.getInstance().subscribeToTopic("allUsers")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Abonnement réussi
                    Log.d(TAG, "Abonnement au topic de notification réussi")
                } else {
                    // Échec de l'abonnement
                    Log.e(TAG, "Échec de l'abonnement au topic de notification", task.exception)
                }
            }
        if(binding.recyclerFragmentHome.isEmpty()) {
            binding.chatlogo.visibility = View.VISIBLE
        }

        if(binding.recyclerFragmentHome.isNotEmpty()) {
            binding.chatlogo.visibility = View.GONE
        }

        var tempList : ArrayList<Event> = ArrayList()
        val searchCategory = SearchCategory()
        val searchByCities = SearchByCities()
        val searchByName = SearchByName()
        var searchStrategy : Strategy

        adapter1 = ListGeneriqueAdapter(R.layout.recycler_events)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerFragmentHome.layoutManager = layoutManager
        binding.recyclerFragmentHome.adapter = adapter1

        viewModel.fetchStrategieEvent()
        viewModel.fetchUserData()


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
        }

        viewModel.user_live.observe(this, Observer { it ->
            Glide.with(binding.imgProfil.context)
                .load(it.img)
                .apply(RequestOptions().override(100, 100))
                .centerCrop()
                .into(binding.imgProfil)
        })



        binding.btnCategory.setOnClickListener{
            var type = "Mars"
            searchStrategy = Strategy(searchCategory)
            strategyEvent(searchStrategy,type)

            binding.chatlogo.visibility = View.GONE


        }

//        FirebaseMessaging.getInstance().token
//            .addOnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w(TAG, "Échec de la récupération du token du dispositif.", task.exception)
//                    return@addOnCompleteListener
//                }
//
//                // Le token du dispositif
//                val token = task.result
//                Log.d(TAG, "Token du dispositif : $token")
//            }




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

    fun strategyEvent(strategy: Strategy, str:String) {
        var tempList : ArrayList<Event> =  ArrayList()
        viewModel.categorieEventList.observe(this, Observer { eventList ->
            tempList = strategy.search(str, eventList) as ArrayList<Event>
            adapter1.addItems(tempList)
        })

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


}











