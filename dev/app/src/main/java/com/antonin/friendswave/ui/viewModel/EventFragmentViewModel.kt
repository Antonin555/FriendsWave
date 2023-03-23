package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.event.AddEventActivity
import com.antonin.friendswave.ui.event.EventsInscritsActivity
import com.antonin.friendswave.ui.event.InterfaceEvent
import com.antonin.friendswave.ui.event.MesEventsActivity
import com.antonin.friendswave.ui.fragment.EventFragment
import com.antonin.friendswave.ui.home.HomeActivity


class EventFragmentViewModel(private val repository:UserRepo):ViewModel() {

    var name: String? = null
    var description: String? = null
    var isPhotoLoad : Boolean? = false
    private var isPublic : Boolean? = false
    var photo: String? = null
    var nbrePersonnes : Int? = 0
    var categorie: String? = ""
    var lattitude: String? = ""
    var longitude : String?  =""
    var date: String? = ""
    var horaire: String? = ""
    var day: Int? = 0
    var month: Int? = 0
    var year:Int? = 0
    var hour:Int? = 0
    var minute: Int? = 0


    val user by lazy {
        repository.currentUser()
    }

    var  interfaceEvent: InterfaceEvent? = null

    private val _eventData = MutableLiveData<Event>()
    val eventData: LiveData<Event> = _eventData

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList

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


    fun goToEventsInscrits(view: View){
        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, EventsInscritsActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun addEventUser(view: View) {
        if(isPublic == true) {
            repository.addEventUserPublic(name!!, isPublic!!,nbrePersonnes!!, user!!.uid, categorie!!, date!!, horaire!!)
        }else {
            repository.addEventUserPrivate(name!!, isPublic=false, nbrePersonnes!!, user!!.uid, categorie!!,date!!, horaire!!)
        }
        Intent(view.context, HomeActivity::class.java).also {
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
        date = day.toString() + "/" + (month + 1).toString() + "/"+ year.toString()
    }

    fun changeHour(hour:Int, minute:Int) {

        horaire = hour.toString() + ":" + minute.toString()
    }


    fun fetchEventsPublic1() {
        repository.fetchEventsPublic1().observeForever{ event ->
        _eventList.value = event
        }
    }

    fun fetchEventsPublic2() {
        repository.fetchEventsPublic2().observeForever{ event ->
            _eventList.value = event
        }

    }


    fun gotoMesEventsActivity(view: View) {
        Intent(view.context, MesEventsActivity::class.java).also {
            view.context.startActivity(it)
        }
    }






}


