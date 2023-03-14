package com.antonin.friendswave.ui.viewModel

import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.fragment.NotifsFragment

class NotifFragmentViewModel (private val repository: UserRepo): ViewModel() {

    fun fetchUsersR() {
        repository.fetchUsersR()
    }

    fun fetchUsersRequest(){
        repository.fetchUsersRequest()
    }

    //pour les notifs p-e faire un NotifFragementViewModel
    fun acceptRequest(position: Int){

        var key = NotifsFragment.user?.friendRequest?.keys?.elementAt(position)
        var email = NotifsFragment.user?.friendRequest?.get(key)

        NotifsFragment.user?.friendList!!.put(key.toString(), email.toString())
        NotifsFragment.user?.friendRequest!!.remove(key)

        if (key != null) {
            repository.acceptRequest(position, key)
        }
    }

    fun refuseRequest(position: Int){
        var key = NotifsFragment.user?.friendRequest?.keys?.elementAt(position)
        NotifsFragment.user?.friendRequest!!.remove(key)

        repository.refuseRequest(position)
    }
}