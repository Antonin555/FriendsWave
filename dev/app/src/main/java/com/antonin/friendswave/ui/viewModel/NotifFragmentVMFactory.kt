package com.antonin.friendswave.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antonin.friendswave.data.repository.UserRepo

class NotifFragmentVMFactory(private val repository: UserRepo) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotifFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotifFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}