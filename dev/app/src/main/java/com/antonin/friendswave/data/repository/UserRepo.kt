package com.antonin.friendswave.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.model.User

class UserRepo(private val firebase: FirebaseSource) {

    fun currentUser() = firebase.currentUser()

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(name : String,email: String, password: String) = firebase.register(name,email, password)

    fun logout() = firebase.logout()

    fun fetchUsersFriends():LiveData<List<User>> {
        val emailUserList = MutableLiveData<List<User>>()

        firebase.fetchUsersFriend() { user ->
            emailUserList.postValue(user)
        }
        return emailUserList
    }

    fun fetchConfirmationEvents():LiveData<List<Event>> {

        val eventListConfirm = MutableLiveData<List<Event>>()

        firebase.fetchConfirmationEvents { event ->
            eventListConfirm.postValue(event)
        }
        return eventListConfirm
    }


    fun fetchUsersRequest():LiveData<List<User>> {
        val notifUserList = MutableLiveData<List<User>>()

        firebase.fetchUsersRequest { notifUser ->
            notifUserList.postValue(notifUser)
        }
        return notifUserList
    }

    fun fetchInvitationEvents() :LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchInvitationEvents { event ->
            eventList.postValue(event)
        }

        return eventList
    }


    fun fetchEventsPublic1() :LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchEventsPublic1 { event ->
            eventList.postValue(event)
        }

        return eventList
    }


    fun fetchEventUser(position: Int) :LiveData<Event> {

        val eventList = MutableLiveData<Event>()

        firebase.fetchEventUser(position) { event ->
            eventList.postValue(event)
        }

        return eventList
    }

//    fun fetchEventsPublic5() = firebase.fetchEventsPublic2()

    fun fetchEventsPrivateUser() :LiveData<List<Event>> {

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchEventsPrivateUser { event ->
            eventList.postValue(event)
        }

        return eventList
    }

    fun fetchEventsPublicUser() : LiveData <List<Event>>{

        val eventList = MutableLiveData<List<Event>>()

        firebase.fetchEventsPublicUser { event ->
            eventList.postValue(event)
        }

        return eventList

    }

    fun fetchGuestDetailEvent(key:String?):LiveData<List<User>>{

        val guestList = MutableLiveData<List<User>>()

        firebase.fetchGuestDetailEvent(key) { user ->
            guestList.postValue(user)
        }
        return guestList
    }

    fun fetchGuestConfirmDetailEventPublic(key: String?):LiveData<List<User>>{

        val confirm_guest_list = MutableLiveData<List<User>>()

        firebase.fetchGuestConfirmDetailEventPublic(key) { user ->
            confirm_guest_list.postValue(user)
        }

        return confirm_guest_list
    }

    fun fetchGuestDetailEventPublic(key:String?):LiveData<List<User>>{

        val guestList = MutableLiveData<List<User>>()

        firebase.fetchGuestDetailEventPublic(key) { user ->
            guestList.postValue(user)
        }
        return guestList
    }


    fun fetchDetailEventPublicUser(key:String?):LiveData<Event>{

        val eventPublicUser = MutableLiveData<Event>()

        firebase.fetchDetailEventPublicUser(key) { event ->
            eventPublicUser.postValue(event)
        }

        return eventPublicUser


    }

    fun fetchDemandeInscriptionEventPublic():LiveData<List<User>>{

        val requestEvent = MutableLiveData<List<User>>()
        firebase.fetchDemandeInscriptionEventPublic { user ->
            requestEvent.postValue(user)
        }
        return requestEvent

    }

    fun getEventData(position: Int) : LiveData<Event> {
        val eventLiveData = MutableLiveData<Event>()

        firebase.getEventData(position) { event ->
            eventLiveData.postValue(event)
        }

        return eventLiveData
    }

    fun addFriendRequestToUser(email: String) = firebase.addFriendRequestToUser(email)



    fun sendAnInvitationPrivateEvent(email: String, position: Int) = firebase.sendAnInvitationPrivateEvent(email,position)

    fun addFriendRequestToUserByUid(uid: String?) = firebase.addFriendRequestToUserByUid(uid)

    fun removeFriend(uid: String?) = firebase.removeFriend(uid)

    fun sendSignalement(uid: String?, messSignalement: String?)  = firebase.sendSignalement(uid,  messSignalement)

    fun getUserData(): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        firebase.getUserData { user ->
            userLiveData.postValue(user)
        }

        return userLiveData
    }

    fun fetchInteret(): LiveData<List<String>?> {
        val interetLiveData = MutableLiveData<List<String>?>()

        firebase.fetchInteret { interet ->
            interetLiveData.postValue(interet)
        }

        return interetLiveData
    }


    fun getAllEventsPendingRequestPublic() : LiveData<List<Event>>{

        val pendingEvents = MutableLiveData<List<Event>>()

        firebase.getAllEventsPendingRequestPublic { event ->
            pendingEvents.postValue(event)
        }
        return pendingEvents
    }


    fun sendRequestToParticipatePublicEvent(idEvent:String, adminEvent:String){

        firebase.sendRequestToParticipatePublicEvent(idEvent,adminEvent)


    }


    fun editProfil(user_live: User?) = firebase.editProfil(user_live)

    fun editEvent(event:Event?)= firebase.editEvent(event)

    fun deleteEvent(event:Event?) = firebase.deleteEvent(event)

    fun getUserProfilData(profilUid: String?): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        firebase.getUserProfilData(profilUid) { user ->
            userLiveData.postValue(user)
        }

        return userLiveData
    }

    fun verifAmitier(profilUid: String?): LiveData<Boolean> {
        val amiLiveData = MutableLiveData<Boolean>()

        firebase.verifAmitier(profilUid) { ami ->
            amiLiveData.postValue(ami)
        }

        return amiLiveData
    }

    fun addEventUserPublic(name: String, isPublic : Boolean, nbrePersonnes:Int, uid: String, category:String, date : String, horaire:String, adress:String,
                           description:String, longitude:String, latitude:String) =
        firebase.addEventUserPublic(name,isPublic,nbrePersonnes, uid, category, date, horaire, adress,description, longitude,latitude)

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int, uid:String,category:String, date : String, horaire:String, adress:String,
    description: String, longitude:String, latitude:String) =
        firebase.addEventUserPrivate(name,isPublic,nbrePersonnes,uid, category, date, horaire, adress, description, longitude, latitude)

    fun acceptRequest1(userNotif: User?){
        firebase.acceptRequestUpdateUser(userNotif)
    }

    fun refuseRequest(userNotif: User?){
        firebase.refuseRequest(userNotif)
    }

    fun refuseInvitationEvent(event:Event?){
        firebase.refuseInvitationEvent(event)
    }

    fun acceptInvitationEvent(event: Event?){
        firebase.acceptInvitationEvent(event)
    }

    fun acceptRequestEvent(user:User?){

        firebase.acceptRequestEvent(user)
    }

    fun declineRequestEvent(user:User?){

        firebase.declineRequestEvent(user)
    }

    fun fetchDiscussion(receiverUid: String):LiveData<List<Messages>>{
        val messageList = MutableLiveData<List<Messages>>()

        firebase.fetchDiscussion(receiverUid) { message ->
            messageList.postValue(message)
        }
        return messageList
    }

    fun fetchfetchEmail():LiveData<List<String>>{
        val emailList = MutableLiveData<List<String>>()

        firebase.fetchEmail() { message ->
            emailList.postValue(message)
        }
        return emailList
    }

    fun addMessagetoDatabase(messageEnvoye: String, receiverUid: String){
        firebase.addMessagetoDatabase(messageEnvoye, receiverUid)
    }

    ///////////////////////////////////Strategie
    fun fetchStrategieEvent():LiveData<List<Event>>{
        val eventList = MutableLiveData<List<Event>>()
        firebase.fetchStrategieEvent { event ->
            eventList.postValue(event)
        }
        return eventList

    }

    fun fetchAllUser():LiveData<List<User>>{
        val userList = MutableLiveData<List<User>>()
        firebase.fetchAllUser { user ->
            userList.postValue(user)
        }
        return userList
    }

}