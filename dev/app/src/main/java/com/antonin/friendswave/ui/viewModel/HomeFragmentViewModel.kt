package com.antonin.friendswave.ui.viewModel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
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
import com.antonin.friendswave.ui.home.SignalementActivity
import java.io.ByteArrayOutputStream

class HomeFragmentViewModel(private val repository: UserRepo):ViewModel() {

    var etudes : String? = ""
    var langue :String? = ""
    var profilUid: String? = ""
    var messSignalement: String? = ""

    private val _user = MutableLiveData<User>()
    var user_live: LiveData<User> = _user

    private val _userProfil = MutableLiveData<User>()
    var user_liveProfil: LiveData<User> = _userProfil

    private val _ami = MutableLiveData<Boolean>()
    var ami_live: LiveData<Boolean> = _ami

    private val _emailUserList = MutableLiveData<List<User>>()
    val emailUserList: LiveData<List<User>> = _emailUserList

    val user by lazy {
        repository.currentUser()
    }

    fun fetchUserData() {
        repository.getUserData().observeForever { user ->
            _user.value = user
        }
    }

    fun fetchUserProfilData(profilUid: String?) {
        repository.getUserProfilData(profilUid).observeForever { user ->
            _userProfil.value = user
        }
    }

    fun verifAmitier(profilUid: String?){
        repository.verifAmitier(profilUid).observeForever { ami ->
            _ami.value = ami
        }
    }

    fun addOrDelete(){
        if(ami_live.value == true){
            repository.removeFriend(profilUid)
        }
        else if (ami_live.value == false){
            repository.addFriendRequestToUserByUid(profilUid)
        }

        verifAmitier(profilUid)
    }

    fun signaler(view: View){
        Intent(view.context, SignalementActivity::class.java).also{
            it.putExtra("uid", profilUid)
            view.context.startActivity(it)
        }
    }

    fun sendSignalement(){
        if (!messSignalement.equals("")){
            repository.sendSignalement(profilUid, messSignalement)
        }

    }


    fun logout(view: View){
        repository.logout()
        view.context.startLoginActivity() // va chercher les fonctions utiles pour les Intent
    }

//    fun fetchUsers() {
//        repository.fetchUsers()
//    }

    fun fetchUsersFriends() {
        repository.fetchUsersFriends().observeForever{ user ->
            _emailUserList.value = user
        }
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