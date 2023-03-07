package com.antonin.friendswave.ui.viewModel

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.antonin.friendswave.data.repository.UserRepo

class HomeFragmentViewModel(private val repository: UserRepo):ViewModel() {


    val user by lazy {
        repository.currentUser()
    }

//    fun logout(view: View){
//        repository.logout()
//        view.context.startLoginActivity() // va chercher les fonctions utiles pour les Intent
//    }
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
//
//    fun fetchEvents1() {
//
//        repository.fetchEvents1()
//    }
//
//    fun addCalendar(event: Event) {
//
//        repository.addEvent( event.name!!,event.date!!, event.prenom!!, event.isActive!!, event.lattitude!!, event.longitude!!)
//    }
}