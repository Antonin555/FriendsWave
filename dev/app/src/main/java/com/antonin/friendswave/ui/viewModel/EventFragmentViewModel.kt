package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.contact.AddContactActivity


class EventFragmentViewModel(private val repository:UserRepo):ViewModel() {



    fun goToAddEvent(view: View){

        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, AddContactActivity::class.java).also {
            view.context.startActivity(it)
        }


    }

}