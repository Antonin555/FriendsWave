package com.antonin.friendswave.ui.viewModel

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.RecyclerView
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.outils.startLoginActivity
import com.antonin.friendswave.ui.authentification.SignupActivity
import com.antonin.friendswave.ui.contact.AddContactActivity

class HomeFragmentViewModel(private val repository: UserRepo):ViewModel() {


    val userName by lazy {
        repository.getUserName()

    }

    val user by lazy {
        repository.currentUser()
    }

//    val userName by lazy {
//        repository.getUserName()
//    }

    fun logout(view: View){
        repository.logout()
        view.context.startLoginActivity() // va chercher les fonctions utiles pour les Intent
    }

    fun fetchUsers() {

        repository.fetchUsers()
    }
//
//    fun addContact(view: View) {
//
//        view.context.startContactActivity()
//    }
//
//    fun fragmentHobbies(view: View) {
//
//        view.context.startProfilFragment()
//    }
//
//    fun fetchEvents(recyclerView: RecyclerView, context: Context, adapter: HomeAdapter) {
//
//        repository.fetchEvents(recyclerView,context,adapter)
//    }
//


//    fun addCalendar(event: Event) {
//
//        repository.addEvent( event.name!!,event.date!!, event.prenom!!, event.isActive!!, event.lattitude!!, event.longitude!!)
//    }

    fun goToAddContact(view: View){

        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, AddContactActivity::class.java).also {
            view.context.startActivity(it)
        }


    }
}