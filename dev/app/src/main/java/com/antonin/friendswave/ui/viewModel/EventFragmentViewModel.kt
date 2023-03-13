package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.event.AddEventActivity


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



    fun fetchEventsPrivate() {


    }

    fun fetchEventsPublic() {

        repository.fetchEventsPublic()
    }

}