package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.R
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.outils.startLoginActivity
import com.antonin.friendswave.ui.contact.AddContactActivity

class HomeFragmentViewModel(private val repository: UserRepo):ViewModel() {

    var etudes : String? = ""
    var langue :String? = ""


    private val _user = MutableLiveData<User>()
    var user_live: LiveData<User> = _user

    fun fetchUserData() {
        repository.getUserData().observeForever { user ->
            _user.value = user
        }
    }

    val user by lazy {
        repository.currentUser()
    }


    fun logout(view: View){
        repository.logout()
        view.context.startLoginActivity() // va chercher les fonctions utiles pour les Intent
    }

//    fun fetchUsers() {
//        repository.fetchUsers()
//    }

    fun fetchUsersFriends() {
        repository.fetchUsersFriends()
    }

    fun goToAddContact(view: View){

        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, AddContactActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun onSelectItem(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

        if (view!!.id == R.id.spinnerEtudes) {

            etudes = parent!!.adapter.getItem(pos).toString()
        }
        if (view.id == R.id.spinnerLangues) {

            langue = parent!!.adapter.getItem(pos).toString()
        }

    }


}