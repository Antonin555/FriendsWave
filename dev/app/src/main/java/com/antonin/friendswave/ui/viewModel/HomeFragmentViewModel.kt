package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.outils.startLoginActivity
import com.antonin.friendswave.strategy.*
import com.antonin.friendswave.ui.contact.AddContactActivity
import com.antonin.friendswave.ui.home.ProfilActivity
import com.antonin.friendswave.ui.home.SignalementActivity


class HomeFragmentViewModel(private val repository: UserRepo):ViewModel() {

    var profilUid: String? = ""
    var messSignalement: String? = ""

    val searchCategory = SearchCategory()
    val searchByCities = SearchByCities()
    val searchByName = SearchByName()
    private lateinit var searchStrategy : Strategy


    private val _user = MutableLiveData<User>()
    var user_live: LiveData<User> = _user

    var etudes : String? = ""
    var langue :String? = ""

    private val _userProfil = MutableLiveData<User>()
    var user_liveProfil: LiveData<User> = _userProfil

    private val _ami = MutableLiveData<Boolean>()
    var ami_live: LiveData<Boolean> = _ami

    private val _emailUserList = MutableLiveData<List<User>>()
    val emailUserList: LiveData<List<User>> = _emailUserList

    private val _categorieEventList = MutableLiveData<List<Event>>()
    val categorieEventList: LiveData<List<Event>> = _categorieEventList

    private val _totalUserList = MutableLiveData<List<User>>()
    val totalUserList: LiveData<List<User>> = _totalUserList

    val user by lazy {
        repository.currentUser()
    }



    private val _interetList = MutableLiveData<List<String>>()
    val interetList: LiveData<List<String>> = _interetList

    fun fetchUserData() {
        repository.getUserData().observeForever { user ->
            _user.value = user
        }
    }

    fun fetchInteret(){
        repository.fetchInteret().observeForever{ interet ->
            _interetList.value = interet
        }
    }

    fun editProfil(){
        repository.editProfil(user_live.value)
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

    fun goToYourProfil(view: View){

        // .also permet d'eviter de déclarer une variable :
        var intent = Intent(view.context, ProfilActivity::class.java)
        intent.putExtra("uid", profilUid)
        view.context.startActivity(intent)

    }

    fun onSelectItem(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        langue = parent!!.adapter.getItem(pos).toString()
        if (user_live.value != null){
            user_live.value!!.langue = langue
        }
    }

    fun onSelectItem2(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        etudes = parent!!.adapter.getItem(pos).toString()
        if (user_live.value != null) {
            user_live.value!!.etude = etudes
        }
    }



    ////////////////////////////////////////////////  Strategie

    fun fetchStrategieEvent(){

        repository.fetchStrategieEvent().observeForever{ event ->
            _categorieEventList.value = event
        }

    }

    fun fetchAllUser(){
        repository.fetchAllUser().observeForever{ user ->
            _totalUserList.value = user
        }
    }


//    fun strategyByCategory() : List<Event> {
//
//        var tempList : List<Event>?
//        var type = "Mars"
//        searchStrategy = Strategy(searchCategory)
//        tempList = searchStrategy.searchByCategory(type, CategorieEventList.value)
//
//        return tempList
//    }



}