package com.antonin.friendswave.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.User

class UserRepo(private val firebase: FirebaseSource) {

    fun currentUser() = firebase.currentUser()

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(name : String,email: String, password: String) = firebase.register(name,email, password)

    fun logout() = firebase.logout()

    fun fetchUsers() = firebase.fetchUsers()




    fun getUser(): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        firebase.getUser() { user ->
            userLiveData.postValue(user)
        }

        return userLiveData
    }



    fun addEventUser(name: String, isPublic : Boolean, nbrePersonnes:Int) =
        firebase.addEventUser(name,isPublic,nbrePersonnes)


}