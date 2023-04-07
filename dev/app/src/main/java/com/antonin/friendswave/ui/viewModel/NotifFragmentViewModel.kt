package com.antonin.friendswave.ui.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo

class NotifFragmentViewModel (private val repository: UserRepo): ViewModel() {


    private val _eventList = MutableLiveData <List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList

    private val _friendNotifList = MutableLiveData <List<User>>()
    val friendNotifList: LiveData<List<User>> = _friendNotifList


    private val _requestListEvent = MutableLiveData<List<User>>()
    val requestListEvent : LiveData<List<User>> = _requestListEvent


    fun fetchUsersRequest(){
        repository.fetchUsersRequest().observeForever{ notifUser ->
            _friendNotifList.value = notifUser
        }

    }

//    fun fetchEventsInvitationByKey(){
//
//        repository.fetchEventsInvitationByKey().observeForever { event->
//            _eventList.value = event
//        }
//    }

    //pour les notifs p-e faire un NotifFragementViewModel
    fun acceptRequest(userNotif: User?){
        repository.acceptRequest1(userNotif)
//        repository.acceptRequest2(key!!, email!!)

    }

    fun refuseRequest(userNotif: User?){
        repository.refuseRequest(userNotif)
    }



    fun refuseInvitationEvent(event:Event?){
        repository.refuseInvitationEvent(event)
    }

    fun acceptInvitationEvent(event:Event?){
        repository.acceptInvitationEvent(event)
    }
    fun acceptRequestEvent(user:User?){

        repository.acceptRequestEvent(user)
    }

    fun fetchEventsInvitation() {
        repository.fetchInvitationEvents().observeForever { event ->
            _eventList.value = event
        }
    }

    fun  fetchDemandeInscriptionEventPublic(){

        repository.fetchDemandeInscriptionEventPublic().observeForever { user->
            _requestListEvent.value = user

        }
    }




}