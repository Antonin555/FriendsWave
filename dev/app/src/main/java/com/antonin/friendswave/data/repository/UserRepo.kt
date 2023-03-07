package com.antonin.friendswave.data.repository

import com.antonin.friendswave.data.firebase.FirebaseSource

class UserRepo(private val firebase: FirebaseSource) {

    fun login(email: String, password: String) = firebase.login(email, password)
}