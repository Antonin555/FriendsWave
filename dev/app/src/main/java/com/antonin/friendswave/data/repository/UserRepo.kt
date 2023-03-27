package com.antonin.friendswave.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.model.User

class UserRepo(private val firebase: FirebaseSource) {

    fun currentUser() = firebase.currentUser()

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(name : String,email: String, password: String) = firebase.register(name,email, password)

    fun logout() = firebase.logout()

    fun fetchUsers() = firebase.fetchUsers()

    fun fetchUsersFriends() = firebase.fetchUsersFriend()

//    fun fetchUsersR() = firebase.fetchUsersR()

    fun fetchUsersRequest(requestList: ArrayList<User>) = firebase.fetchUsersRequest(requestList)

    fun fetchInvitationEvents() :LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchInvitationEvents { event ->
            eventList.postValue(event)
        }

        return eventList
    }

//    fun fetchEventsRequest(eventList:ArrayList<Event>) = firebase.fetchEventsRequest(eventList)


    fun fetchEventsPublic1() :LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchEventsPublic1 { event ->
            eventList.postValue(event)
        }

        return eventList
    }


    fun fetchEventUser(position: Int) :LiveData<Event> {

        val eventList = MutableLiveData<Event>()

        firebase.fetchEventUser(position) { event ->
            eventList.postValue(event)
        }

        return eventList
    }

//    fun fetchEventsPublic5() = firebase.fetchEventsPublic2()

    fun fetchEventsPrivateUser() :LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchEventsPrivateUser { event ->
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



    fun sendAnInvitationPrivateEvent(email: String, position: Int) = firebase.sendAnInvitationPrivateEvent(email,position)

    fun getUserData(): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        firebase.getUserData { user ->
            userLiveData.postValue(user)
        }

        return userLiveData

    }

    fun addEventUserPublic(name: String, isPublic : Boolean, nbrePersonnes:Int, uid: String, category:String, date : String, horaire:String) =
        firebase.addEventUserPublic(name,isPublic,nbrePersonnes, uid, category, date, horaire)

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int, uid:String,category:String, date : String, horaire:String) =
        firebase.addEventUserPrivate(name,isPublic,nbrePersonnes,uid, category, date, horaire)

    fun acceptRequest1(position: Int){
        firebase.acceptRequestUpdateUser(position)
    }

    fun refuseRequest(position: Int){
        firebase.refuseRequest(position)
    }

    fun fetchDiscussion(receiverUid: String):LiveData<List<Messages>>{
        val messageList = MutableLiveData<List<Messages>>()

        firebase.fetchDiscussion(receiverUid) { message ->
            messageList.postValue(message)
        }
        return messageList
    }

    fun fetchfetchEmail():LiveData<List<String>>{
        val emailList = MutableLiveData<List<String>>()

        firebase.fetchEmail() { message ->
            emailList.postValue(message)
        }
        return emailList
    }

    fun addMessagetoDatabase(messageEnvoye: String, receiverUid: String){
        firebase.addMessagetoDatabase(messageEnvoye, receiverUid)
    }


}