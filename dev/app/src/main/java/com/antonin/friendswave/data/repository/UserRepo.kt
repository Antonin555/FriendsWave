package com.antonin.friendswave.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User

class UserRepo(private val firebase: FirebaseSource) {

    fun currentUser() = firebase.currentUser()

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(name : String,email: String, password: String) = firebase.register(name,email, password)

    fun logout() = firebase.logout()

    fun fetchUsers() = firebase.fetchUsers()

    fun fetchUsersR() = firebase.fetchUsersR()

    fun fetchUsersRequest() = firebase.fetchUsersRequest()

    fun fetchEventsPublic() = firebase.fetchEventsPublic()


    fun fetchEventsPublic1(eventList:ArrayList<Event>) = firebase.fetchEventsPublic1(eventList)

    fun fetchOneEvent(): LiveData<Event> {
        val eventLiveData = MutableLiveData<Event>()

        firebase.fetchOneEvent { event ->
            eventLiveData.postValue(event)
        }

        return eventLiveData

    }

    fun addFriendRequestToUser(email: String) = firebase.addFriendRequestToUser(email)

    fun getUserData(): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        firebase.getUserData { user ->
            userLiveData.postValue(user)
        }

        return userLiveData

    }

    fun addEventUserPublic(name: String, isPublic : Boolean, nbrePersonnes:Int) =
        firebase.addEventUserPublic(name,isPublic,nbrePersonnes)

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int) =
        firebase.addEventUserPrivate(name,isPublic,nbrePersonnes)

    fun acceptRequest(key: String, email: String){
        firebase.acceptRequest(key, email)
    }

    fun refuseRequest(position: Int){
        firebase.refuseRequest(position)
    }


}