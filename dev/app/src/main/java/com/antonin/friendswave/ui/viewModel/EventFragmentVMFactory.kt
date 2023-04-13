package com.antonin.friendswave.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo

@Suppress("UNCHECKED_CAST")

class EventFragmentVMFactory (private val repository: UserRepo, private val repoEvent: EventRepo) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventFragmentViewModel(repository, repoEvent) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }



}