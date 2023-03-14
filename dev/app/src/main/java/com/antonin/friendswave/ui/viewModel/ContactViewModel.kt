package com.antonin.friendswave.ui.viewModel

import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.repository.UserRepo

class ContactViewModel(private val repository: UserRepo) : ViewModel() {

    var email: String? = null

    val user by lazy {
        repository.currentUser()
    }

    fun addFriendRequestToUser(){

        if (email.isNullOrEmpty()) {

            //faire un interface pour indiquer les erreurs
//            interfaceAuth?.onFailure("Please enter a valid mail and a valid password")
            return
        }

        repository.addFriendRequestToUser(email!!)

    }
}