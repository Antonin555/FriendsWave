package com.antonin.friendswave.ui.authentification

interface InterfaceAuth {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}