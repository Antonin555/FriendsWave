package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.event.*


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


    val user by lazy {
        repository.currentUser()
    }

    var  interfaceEvent: InterfaceEvent? = null

    private val _eventData = MutableLiveData<Event>()
    val eventData: LiveData<Event> = _eventData

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

    fun addEventUser() {
        if(isPublic == true) {
            repository.addEventUserPublic(name!!, isPublic!!,nbrePersonnes!!, user!!.uid)
        }else {
            repository.addEventUserPrivate(name!!, isPublic=false, nbrePersonnes!!, user!!.uid)
        }
    }

    val isChecked: MutableLiveData<Boolean> = MutableLiveData()
    fun executeOnStatusChanged(switch: CompoundButton, isChecked: Boolean) {
        isPublic = isChecked
    }




    // ViewModel pour MyEvent :



    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList


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


