package com.antonin.friendswave.ui.viewModel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo


class ChatViewModel(private val repository: UserRepo, private val repoEvent: EventRepo): ViewModel()  {
    var messageEnvoye: String? = ""
    var receiverUid: String? = ""
    val messageGroup = MutableLiveData<String>()

    var messageEnvoyeGroup: String? = ""
    var receiverUidGroup: String? = ""
    val message = MutableLiveData<String>()
    //chat
    private val _messageList = MutableLiveData<List<Messages>>()
    val messageList: LiveData<List<Messages>> = _messageList
    //group chat
    private val _groupMessageList = MutableLiveData<List<Messages>>()
    val groupMessageList: LiveData<List<Messages>> = _groupMessageList
    //pour l'event du groupChat
    private val _mainEvent = MutableLiveData<Event>()
    val mainEvent: LiveData<Event> = _mainEvent

    private val _user = MutableLiveData<User>()
    var user_live: LiveData<User> = _user

    fun addMessagetoDatabase(view: View){
        println(view)
        if(messageEnvoye != null){
            repository.addMessagetoDatabase(messageEnvoye!!, receiverUid!!, user_live.value?.name.toString())

        }
        messageEnvoye = ""
        message.value = ""
        fetchDiscussion()
    }

    fun addMessageGrouptoDatabase(view: View){
        println(view)
        if(messageEnvoye != null){
            repository.addMessageGrouptoDatabase(messageEnvoyeGroup!!, receiverUidGroup!!, user_live.value?.name.toString())

        }
        messageEnvoyeGroup = ""
        messageGroup.value = ""
        fetchDiscussionGroup()
    }


    fun fetchDiscussion() {
        repository.fetchDiscussion(receiverUid!!).observeForever{ message ->
            _messageList.value = message
        }
    }
    //a changer repetission
    fun fetchDiscussionGroup() {
        repository.fetchDiscussionGroup(receiverUidGroup!!).observeForever{ message ->
            _groupMessageList.value = message
        }
    }

    fun fetchSpecificEvents(hostId: String, eventKey: String)
    {
        repoEvent.fetchSpecificEvents(hostId, eventKey).observeForever{ event ->
            _mainEvent.value = event
        }
    }

    fun fetchUserData() {
        repository.getUserData().observeForever { user ->
            _user.value = user
        }
    }
}