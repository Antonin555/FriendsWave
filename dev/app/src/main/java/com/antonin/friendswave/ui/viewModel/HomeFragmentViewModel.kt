package com.antonin.friendswave.ui.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.outils.startHomeActivity
import com.antonin.friendswave.outils.startLoginActivity
import com.antonin.friendswave.strategy.*
import com.antonin.friendswave.ui.contact.AddContactActivity
import com.antonin.friendswave.ui.home.ManageHomeActivity
import com.antonin.friendswave.ui.home.ProfilActivity
import com.antonin.friendswave.ui.home.SignalementActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class HomeFragmentViewModel(private val repository: UserRepo, private val repoEvent:EventRepo):ViewModel() {

    var profilUid: String? = ""
    var messSignalement: String? = ""
    var day: Int? = 0
    var month: Int? = 0
    var year:Int? = 0
    var date: String? = ""

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun editProfil(view: View){

        if(!verificatioAge(user_live.value!!.date!!)){
            val toast = Toast.makeText(view.context, "Votre date de naissance n'est pas valide", Toast.LENGTH_SHORT)
            toast.show()
        }
        else{
            repository.editProfil(user_live.value)

            val toast = Toast.makeText(view.context, "Votre profil a bien été modifié!", Toast.LENGTH_SHORT)
            toast.show()

            Intent(view.context, ManageHomeActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                view.context.startActivity(it)
            }
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
        //Alex pour gerer le cycle de vie des activity
        val activity = view.context as Activity
        activity.finish()
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

        repoEvent.fetchStrategieEvent().observeForever{ event ->
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

    fun changeDate(year: Int, month: Int, day: Int) {



        val dayString =  if (day < 10) "0$day" else day.toString()
        val monthString = if (month <= 10) "0${month + 1}" else "${month + 1}"

        date = dayString + "/"+monthString +"/"+ year.toString()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun verificatioAge(date: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateNaissance = LocalDate.parse(date, formatter)
        val age = ChronoUnit.YEARS.between(dateNaissance, LocalDate.now())
        val estMajeur = age >= 18 && dateNaissance.isBefore(LocalDate.now())
        return estMajeur
    }


    fun fetchUserByMail(mail:String){

        repository.fetchUserByMail(mail)
    }

    fun registerPhoto(photo: Uri, context: Context){
        user_live.value?.img = repository.registerPhoto(photo, context)

    }
    fun registerPhotoCover(photo: Uri, context: Context){
        user_live.value?.imgCover = repository.registerPhotoCover(photo, context)

    }

}