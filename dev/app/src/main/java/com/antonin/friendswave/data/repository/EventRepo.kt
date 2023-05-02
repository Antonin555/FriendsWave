package com.antonin.friendswave.data.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User

class EventRepo(private val firebaseEvent: FirebaseSourceEvent) {

    fun currentUser() = firebaseEvent.currentUser()


    fun fetchConfirmationEvents(): LiveData<List<Event>> {

        val eventListConfirm = MutableLiveData<List<Event>>()

        firebaseEvent.fetchConfirmationEvents { event ->
            eventListConfirm.postValue(event)
        }
        return eventListConfirm
    }


    ///////////////////////////////////Strategie
    fun fetchStrategieEvent():LiveData<List<Event>>{
        val eventList = MutableLiveData<List<Event>>()
        firebaseEvent.fetchStrategieEvent { event ->
            eventList.postValue(event)
        }
        return eventList

    }
    fun fetchSpecificEvents(hostId: String, eventKey: String):LiveData<Event>{
        val event = MutableLiveData<Event>()
        firebaseEvent.fetchSpecificEvents(hostId, eventKey) { user ->
            event.postValue(user)
        }
        return event
    }

    fun fetchInvitationEvents() : LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebaseEvent.fetchInvitationEvents { event ->
            eventList.postValue(event)
        }

        return eventList
    }

    fun refuseInvitationEvent(event:Event?){
        firebaseEvent.refuseInvitationEvent(event)
    }

    fun acceptInvitationEvent(event: Event?){
        firebaseEvent.acceptInvitationEvent(event)
    }

    fun acceptRequestEvent(user:User?){

        firebaseEvent.acceptRequestEvent(user)
    }

    fun declineRequestEvent(user:User?){

        firebaseEvent.declineRequestEvent(user)
    }


    fun fetchEventsPublic1() : LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebaseEvent.fetchEventsPublic1 { event ->
            eventList.postValue(event)
        }

        return eventList
    }


    fun fetchEventUser(position: Int) : LiveData<Event> {

        val eventList = MutableLiveData<Event>()

        firebaseEvent.fetchEventUser(position) { event ->
            eventList.postValue(event)
        }

        return eventList
    }

    fun addEventUserPublic(
        name: String, isPublic: Boolean, nbrePersonnes:Int, uid: String, category:String, date: String, horaire:String, adress:String,
        description:String, longitude: String, latitude: String, photo:Uri, context: Context
    ) =
        firebaseEvent.addEventUserPublic(name,isPublic,nbrePersonnes, uid, category, date, horaire, adress,description, longitude,latitude, photo, context)

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int, uid:String,category:String, date : String, horaire:String, adress:String,
                            description: String, longitude:String, latitude:String, photo: Uri, context: Context) =
        firebaseEvent.addEventUserPrivate(name,isPublic,nbrePersonnes,uid, category, date, horaire, adress, description, longitude, latitude, photo, context)

    fun editEvent(event:Event?)= firebaseEvent.editEvent(event)

    fun deleteEvent(event:Event?) = firebaseEvent.deleteEvent(event)
//    fun fetchEventsPublic5() = firebase.fetchEventsPublic2()
    fun deleteConfirmation(event: Event?) = firebaseEvent.deleteConfirmation(event)

    fun deletePendingEvent(event: Event?) = firebaseEvent.deletePendingEvent(event)


    fun fetchEventsPrivateUser() : LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebaseEvent.fetchEventsPrivateUser { event ->
            eventList.postValue(event)
        }

        return eventList
    }

    fun sendRequestToParticipatePublicEvent(idEvent:String, adminEvent:String){

        firebaseEvent.sendRequestToParticipatePublicEvent(idEvent,adminEvent)


    }

    fun getAllEventsPendingRequestPublic() : LiveData<List<Event>>{

        val pendingEvents = MutableLiveData<List<Event>>()

        firebaseEvent.getAllEventsPendingRequestPublic { event ->
            pendingEvents.postValue(event)
        }
        return pendingEvents
    }

    fun sendAnInvitationEvent(email: String, event: Event) = firebaseEvent.sendAnInvitationEvent(event,email)

    fun fetchParticipantAttente(key:  String): LiveData<List<User>>{
        val usertList = MutableLiveData<List<User>>()

//        firebaseEvent.fetchParticipantAttente(key) { user ->
//            usertList.postValue(user as List<User>?)
//        }

        return usertList
    }

    fun fetchEventsPublicUser() : LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebaseEvent.fetchEventsPublicUser { event ->
            eventList.postValue(event)
        }

        return eventList

    }

    fun fetchGuestDetailEvent(key:String?): LiveData<List<User>> {

        val guestList = MutableLiveData<List<User>>()

        firebaseEvent.fetchGuestDetailEvent(key) { user ->
            guestList.postValue(user)
        }
        return guestList
    }

    fun fetchGuestAttenteEventPrive(key:String?): LiveData<List<User>> {

        val guestList = MutableLiveData<List<User>>()

        firebaseEvent.fetchGuestAttenteEventPrive(key) { user ->
            guestList.postValue(user)
        }
        return guestList
    }



    fun fetchGuestConfirmDetailEventPublic(key: String?): LiveData<List<User>> {

        val confirm_guest_list = MutableLiveData<List<User>>()

        firebaseEvent.fetchGuestConfirmDetailEventPublic(key) { user ->
            confirm_guest_list.postValue(user)
        }

        return confirm_guest_list
    }

    fun fetchGuestDetailEventPublic(key:String?): LiveData<List<User>> {

        val guestList = MutableLiveData<List<User>>()

        firebaseEvent.fetchGuestDetailEventPublic(key) { user ->
            guestList.postValue(user)
        }
        return guestList
    }


    fun fetchDetailEventPublicUser(key:String?): LiveData<Event> {

        val eventPublicUser = MutableLiveData<Event>()

        firebaseEvent.fetchDetailEventPublicUser(key) { event ->
            eventPublicUser.postValue(event)
        }

        return eventPublicUser


    }

    fun fetchDemandeInscriptionEventPublic(): LiveData<List<User>> {

        val requestEvent = MutableLiveData<List<User>>()
        firebaseEvent.fetchDemandeInscriptionEventPublic { user ->
            requestEvent.postValue(user)
        }
        return requestEvent

    }

    fun getEventData(key: String) : LiveData<Event> {
        val eventLiveData = MutableLiveData<Event>()

        firebaseEvent.getEventData(key) { event ->
            eventLiveData.postValue(event)
        }

        return eventLiveData
    }

//    fun registerPhotoEvent(photo: Uri, context: Context): String{
//
//        return firebaseEvent.registerPhotoEvent(photo, context)
//    }


}