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


//    private val disposables = CompositeDisposable()

//    private val _user = MutableLiveData<User>()
//    var user_live: LiveData<User> = _user
//
//    fun fetchUserData() {
//        repository.getUserData().observeForever { user ->
//            _user.value = user
//        }
//
//    }

//    fun fetchUsersR() {
//        repository.fetchUsersR()
//    }

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

    fun fetchEventsInvitation() {
        repository.fetchInvitationEvents().observeForever { event ->
            _eventList.value = event
        }
    }

//    fun fetchEventsInvitation(eventList:ArrayList<Event>) {
//
//        repository.fetchInvitationEvents(eventList)
//    }


}