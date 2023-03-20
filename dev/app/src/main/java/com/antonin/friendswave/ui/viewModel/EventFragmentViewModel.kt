package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.outils.startMesEventsActivity
import com.antonin.friendswave.ui.authentification.InterfaceAuth
import com.antonin.friendswave.ui.event.AddEventActivity
import com.antonin.friendswave.ui.event.InterfaceEvent
import com.antonin.friendswave.ui.event.MyEventActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers


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

    fun addEventUser() {
        if(isPublic == true) {
            repository.addEventUserPublic(name!!, isPublic!!,nbrePersonnes!!)
        }else {
            repository.addEventUserPrivate(name!!, isPublic=false, nbrePersonnes!!)
        }
    }

    val isChecked: MutableLiveData<Boolean> = MutableLiveData()
    fun executeOnStatusChanged(switch: CompoundButton, isChecked: Boolean) {
        isPublic = isChecked
    }

//    private val _event = MutableLiveData<Event>()
//    var event_live: LiveData<Event> = _event

//    fun fetchEventsPublic() {
//        repository.fetchEventsPublic()
//    }







    // ViewModel pour MyEvent :



    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList

//    init {
//        fetchEventsPublic2()
//    }
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

    private val disposables = CompositeDisposable()



    fun gotoMyEventActivity(view: View) {
        Intent(view.context, MyEventActivity::class.java).also {
            view.context.startActivity(it)
        }
    }


//    fun fetchEventsPublic2() {
////        val disposable = repository.fetchEventsPublic2(eventList).subscribeOn(io.reactivex.schedulers.Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
//
//        val disposable = repository.fetchEventsPublic2().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
//            interfaceAuth?.checkContent()
//        }, {
//            //sending a failure callback
//
//
//        })
//        disposables.add(disposable)
//    }

    //disposing the disposables
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}


