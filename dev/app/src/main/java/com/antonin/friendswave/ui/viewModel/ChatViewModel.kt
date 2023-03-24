package com.antonin.friendswave.ui.viewModel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Message
import com.antonin.friendswave.data.repository.UserRepo


class ChatViewModel(private val repository: UserRepo): ViewModel()  {
    var messageEnvoye: String? = ""
    var receiverUid: String? = ""

    fun addMessagetoDatabase(view: View){
        println(view)
        if(messageEnvoye != null){
            repository.addMessagetoDatabase(messageEnvoye!!, receiverUid!!)

        }
        messageEnvoye = ""
    }

    private val _messageList = MutableLiveData<List<Message>>()
    val messageList: LiveData<List<Message>> = _messageList

    fun fetchDiscussion(){
        repository.fetchDiscussion(receiverUid!!).observeForever{ message ->
            _messageList.value = message
        }
    }

//    fun remiseAZero(){
//        messageEnvoye = ""
//    }
}