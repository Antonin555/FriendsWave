package com.antonin.friendswave.data.firebase

import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceIDService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)


    }

}