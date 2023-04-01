package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.event.*
import com.antonin.friendswave.ui.home.ManageHomeActivity
import java.text.SimpleDateFormat
import java.util.*


class EventFragmentViewModel(private val repository:UserRepo):ViewModel() {

    var name: String? = null
    var description: String? = null
    var isPhotoLoad : Boolean? = false
    private var isPublic : Boolean? = false
    var photo: String? = null
    var nbrePersonnes : Int? = 0
    var categorie: String? = ""
    var adress: String? = ""
    var lattitude: String? = ""
    var longitude : String?  =""
    var date: String? = ""
    var horaire: String? = ""
    var day: Int? = 0
    var month: Int? = 0
    var year:Int? = 0
    var hour:Int? = 0
    var minute: Int? = 0

    var email: String? = null

    val formatter = SimpleDateFormat("dd/MM/yyyy")
    var dateFormat  = Date()
//    val currentDate = formatter.parse(dateFormat.toString())

    val user by lazy {
        repository.currentUser()
    }

    private val _guestList = MutableLiveData<List<User>>()
    val guestList: LiveData<List<User>> = _guestList

    private val _guestListPublic = MutableLiveData<List<User>>()
    val guestListPublic: LiveData<List<User>> = _guestListPublic

    var  interfaceEvent: InterfaceEvent? = null

    private val _eventData = MutableLiveData<Event>()
    val eventData: LiveData<Event> = _eventData


    private val _eventDataUser = MutableLiveData<Event>()
    val eventDataUser: LiveData<Event> = _eventDataUser


    private val _eventPublicUser = MutableLiveData<Event>()
    val eventPublicUser: LiveData<Event> = _eventPublicUser

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList

    private val _eventListPublicUser = MutableLiveData<List<Event>>()
    val eventListPublicUser : LiveData<List<Event>> = _eventListPublicUser

    private val _eventListConfirm = MutableLiveData<List<Event>>()
    val eventListConfirm: LiveData<List<Event>> = _eventListConfirm

    fun fetchDataEvent(position: Int) {
        repository.getEventData(position).observeForever { event ->
            _eventData.value = event
        }
    }

    fun goToAddEvent(view: View){
        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, AddEventActivity::class.java).also {
            view.context.startActivity(it)
        }
    }


    fun addEventUser(view: View) {
        if(isPublic == true) {
            repository.addEventUserPublic(name!!, isPublic!!,nbrePersonnes!!, user!!.uid, categorie!!, date!!, horaire!!, adress!!)
        }else {
            repository.addEventUserPrivate(name!!, isPublic=false, nbrePersonnes!!, user!!.uid, categorie!!,date!!, horaire!!, adress!!)
        }
        Toast.makeText(view.context,"Evenement en cours de publication", Toast.LENGTH_LONG).show()

        Intent(view.context, ManageHomeActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    val isChecked: MutableLiveData<Boolean> = MutableLiveData()
    fun executeOnStatusChanged(switch: CompoundButton, isChecked: Boolean) {
        isPublic = isChecked
    }

    // pour recuperer la valeur de la categorie dans le spinner :
    fun onSelectItem(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
       categorie = parent!!.adapter.getItem(pos).toString()
    }

    // ViewModel pour MyEvent :

    fun changeDate(year: Int, month: Int, day: Int) {

        var dayString : String = ""
        var monthString : String = ""

        if(day < 10) {
            dayString = "0" + day.toString()
        } else
            dayString = day.toString()

        if(month < 10) {
            monthString = "0"+(month + 1).toString()
        } else
            monthString = (month+1).toString()

        date = dayString + "/" + monthString + "/"+ year.toString()

        val dateEvent : Date = formatter.parse(date)

//        if(dateEvent.before(currentDate)){
//
//            println("Impossible de revenir dans le passé")
//
//        }
        date = dateEvent.toString()

    }

    fun changeHour(hour:Int, minute:Int) {

        var hourString : String = ""
        var minuteString : String = ""

        if(hour < 10 || minute < 10){

            hourString = "0" + hour.toString()
            minuteString = "0" + minute.toString()

        }else {

            hourString = hour.toString()
            minuteString = minute.toString()
        }

        horaire = hourString + ":" + minuteString
    }

    fun fetchGuestDetailEvent(key:String?){
        repository.fetchGuestDetailEvent(key).observeForever{ event ->
            _guestList.value = event
        }

    }

    fun fetchGuestDetailEventPublic(key:String?){
        repository.fetchGuestDetailEventPublic(key).observeForever{ event->
            _guestListPublic.value = event
        }

    }

    fun fetchEventsPublic1() {
        repository.fetchEventsPublic1().observeForever{ event ->
        _eventList.value = event
        }
    }

    fun fetchEventsPrivateUser() {
        repository.fetchEventsPrivateUser().observeForever{ event ->
            _eventList.value = event
        }

    }


    fun fetchEventsPublicUser() {
        repository.fetchEventsPublicUser().observeForever{ event ->
            _eventListPublicUser.value = event
        }

    }

    fun fetchConfirmationEvents() {

        repository.fetchConfirmationEvents().observeForever{event ->
            _eventListConfirm.value = event
        }

    }

    fun fetchEventUser(pos: Int) {
        repository.fetchEventUser(pos).observeForever{ event ->
            _eventDataUser.value = event
        }
    }


    fun fetchDetailEventPublicUser(pos: Int) {
        repository.fetchDetailEventPublicUser(pos).observeForever{ event ->
            _eventPublicUser.value = event
        }
    }

    fun sendAnInvitationPrivateEvent(pos: Int){

        if (email.isNullOrEmpty()) {
            return
        }

        repository.sendAnInvitationPrivateEvent(email!!, pos!!)

    }





    fun gotoMesEventsActivity(view: View) {
        Intent(view.context, ManagerFragmentEvent::class.java).also {
            view.context.startActivity(it)
        }
    }






}


