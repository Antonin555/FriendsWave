package com.antonin.friendswave.data.repository

import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.firebase.FirebaseSource

class UserRepo(private val firebase: FirebaseSource) {

    fun currentUser() = firebase.currentUser()

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(name : String,email: String, password: String) = firebase.register(name,email, password)

    fun logout() = firebase.logout()

    fun fetchUsers() = firebase.fetchUsers()

    fun getUserName() = firebase.getUserName()
}