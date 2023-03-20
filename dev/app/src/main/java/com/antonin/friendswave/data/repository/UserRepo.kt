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

//    fun fetchUsersR() = firebase.fetchUsersR()

    fun fetchUsersRequest(requestList: ArrayList<User>) = firebase.fetchUsersRequest(requestList)

//    fun fetchEventsPublic() = firebase.fetchEventsPublic()


    fun fetchEventsPublic1() :LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchEventsPublic1() { event ->
            eventList.postValue(event)
        }

        return eventList
    }

//    fun fetchEventsPublic5() = firebase.fetchEventsPublic2()

    fun fetchEventsPublic2() :LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchEventsPublic2() { event ->
            eventList.postValue(event)
        }

        return eventList
    }

    fun getEventData(position: Int) : LiveData<Event> {
        val eventLiveData = MutableLiveData<Event>()

        firebase.getEventData(position) { event ->
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

    fun acceptRequest1(position: Int){
        firebase.acceptRequestUpdateUser1(position)
    }

    fun acceptRequest2(key: String, email: String){
        firebase.acceptRequests(key, email)
    }

    fun refuseRequest(position: Int){
        firebase.refuseRequest(position)
    }


}