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
import com.antonin.friendswave.ui.event.AddEventActivity
import com.antonin.friendswave.ui.event.InterfaceEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

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

    private val _event = MutableLiveData<Event>()
    var event_live: LiveData<Event> = _event

    fun fetchEventsPublic() {
        repository.fetchEventsPublic()
    }

    fun fetchEventsPublic1(eventList:ArrayList<Event>) {
        repository.fetchEventsPublic1(eventList)
    }

}