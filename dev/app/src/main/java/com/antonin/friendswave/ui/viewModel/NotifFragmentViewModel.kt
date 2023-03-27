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

    fun fetchUsersRequest(requestList:ArrayList<User>){
        repository.fetchUsersRequest(requestList)
    }

    //pour les notifs p-e faire un NotifFragementViewModel
    fun acceptRequest(position: Int){
        repository.acceptRequest1(position)
//        repository.acceptRequest2(key!!, email!!)

    }

    fun refuseRequest(position: Int){
        repository.refuseRequest(position)
    }

//    fun fetchEventsInvitation() {
//        repository.fetchInvitationEvents().observeForever { event ->
//            _eventList.value = event
//        }
//    }
    fun fetchEventsInvitation(eventList:ArrayList<Event>) {

        repository.fetchInvitationEvents(eventList)
    }

//    //disposing the disposables
//    override fun onCleared() {
//        super.onCleared()
//        disposables.dispose()
//    }
}