package com.antonin.friendswave.ui.viewModel

import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.repository.UserRepo
import com.google.firebase.database.R


class ChatViewModel(private val repository: UserRepo): ViewModel()  {
    var messageEnvoye: String? = ""
    var receiverUid: String? = ""
    val message = MutableLiveData<String>()
    //chat
    private val _messageList = MutableLiveData<List<Messages>>()
    val messageList: LiveData<List<Messages>> = _messageList
    //group chat
    private val _groupMessageList = MutableLiveData<List<Messages>>()
    val groupMessageList: LiveData<List<Messages>> = _groupMessageList

    fun addMessagetoDatabase(view: View){
        println(view)
        if(messageEnvoye != null){
            repository.addMessagetoDatabase(messageEnvoye!!, receiverUid!!)

        }
        messageEnvoye = ""
        message.value = ""
        fetchDiscussion()
    }

    fun fetchDiscussion() {
        repository.fetchDiscussion(receiverUid!!).observeForever{ message ->
            _messageList.value = message
        }
    }

}