package com.antonin.friendswave.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.model.User

class UserRepo( private val firebaseUser: FirebaseSourceUser) {

    fun currentUser() = firebaseUser.currentUser()

    fun login(email: String, password: String) = firebaseUser.login(email, password)



    fun register(
        name: String,
        email: String,
        password: String,
        familyName: String,
        nickname: String,
        city: String,
        date: String
    ) = firebaseUser.register(name,email, password, familyName, nickname, city, date)

    fun fetchAllPseudo():LiveData<List<String>> {
        val pseudoList = MutableLiveData<List<String>>()

        firebaseUser.fetchAllPseudo(){ pseudo ->
            pseudoList.postValue(pseudo)
        }

        return pseudoList
    }

    fun logout() = firebaseUser.logout()

    fun fetchUsersFriends():LiveData<List<User>> {
        val emailUserList = MutableLiveData<List<User>>()

        firebaseUser.fetchUsersFriend() { user ->
            emailUserList.postValue(user)
        }
        return emailUserList
    }


    fun fetchUsersRequest(): LiveData<List<User>> {
        val notifUserList = MutableLiveData<List<User>>()

        firebaseUser.fetchUsersRequest { notifUser ->
            notifUserList.postValue(notifUser)
        }
        return notifUserList
    }

    fun addFriendRequestToUser(email: String) = firebaseUser.addFriendRequestToUser(email)


    fun requestAlreadySend(email: String):LiveData<Boolean>{
        val doubleRequest = MutableLiveData<Boolean>()
        firebaseUser.requestAlreadySend(email) { bool ->
            doubleRequest.postValue(bool)
        }
        return doubleRequest
    }


    fun addFriendRequestToUserByUid(uid: String?) = firebaseUser.addFriendRequestToUserByUid(uid)

    fun removeFriend(uid: String?) = firebaseUser.removeFriend(uid)

    fun sendSignalement(uid: String?, messSignalement: String?)  = firebaseUser.sendSignalement(uid,  messSignalement)

    fun getUserData(): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        firebaseUser.getUserData { user ->
            userLiveData.postValue(user)
        }

        return userLiveData
    }

    fun fetchInteret(): LiveData<List<String>?> {
        val interetLiveData = MutableLiveData<List<String>?>()

        firebaseUser.fetchInteret { interet ->
            interetLiveData.postValue(interet)
        }

        return interetLiveData
    }








    fun editProfil(user_live: User?) = firebaseUser.editProfil(user_live)



    fun getUserProfilData(profilUid: String?): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        firebaseUser.getUserProfilData(profilUid) { user ->
            userLiveData.postValue(user)
        }

        return userLiveData
    }

    fun verifAmitier(profilUid: String?): LiveData<Boolean> {
        val amiLiveData = MutableLiveData<Boolean>()

        firebaseUser.verifAmitier(profilUid) { ami ->
            amiLiveData.postValue(ami)
        }

        return amiLiveData
    }



    fun acceptRequest1(userNotif: User?){
        firebaseUser.acceptRequestUpdateUser(userNotif)
    }

    fun refuseRequest(userNotif: User?){
        firebaseUser.refuseRequest(userNotif)
    }

    fun declineRequestEvent(user:User?){

        firebaseUser.declineRequestEvent(user)
    }

    fun fetchDiscussion(receiverUid: String):LiveData<List<Messages>>{
        val messageList = MutableLiveData<List<Messages>>()

        firebaseUser.fetchDiscussion(receiverUid) { message ->
            messageList.postValue(message)
        }
        return messageList
    }

    fun fetchDiscussionGroup(receiverUid: String):LiveData<List<Messages>>{
        val messageList = MutableLiveData<List<Messages>>()

        firebaseUser.fetchDiscussionGroup(receiverUid) { message ->
            messageList.postValue(message)
        }
        return messageList
    }

    fun fetchfetchEmail():LiveData<List<String>>{
        val emailList = MutableLiveData<List<String>>()

        firebaseUser.fetchEmail() { message ->
            emailList.postValue(message)
        }
        return emailList
    }

    fun addMessagetoDatabase(messageEnvoye: String, receiverUid: String, userName: String){
        firebaseUser.addMessagetoDatabase(messageEnvoye, receiverUid, userName)
    }

    fun addMessageGrouptoDatabase(messageEnvoye: String, receiverUid: String, userName: String){
        firebaseUser.addMessageGrouptoDatabase(messageEnvoye, receiverUid, userName)
    }


    fun fetchAllUser():LiveData<List<User>>{
        val userList = MutableLiveData<List<User>>()
        firebaseUser.fetchAllUser { user ->
            userList.postValue(user)
        }
        return userList
    }



}