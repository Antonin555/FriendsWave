package com.antonin.friendswave.data.firebase

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.antonin.friendswave.outils.startHomeActivity

class FirebaseSource {


    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().getReference()

    fun currentUser() = firebaseAuth.currentUser


    fun logout() = firebaseAuth.signOut()

    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                println("yo1")
            }
            else
                println("yo-bad")
        }
    }


//
//    fun addUserToDatabase(name: String, email: String, uid: String, ){
//
//
//
//    }

    fun register(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                //code for jumping to home

                firebaseData.push().setValue(User(name,email,firebaseAuth.currentUser?.uid!!))
//                addUserToDatabase(name,email, firebaseAuth.currentUser?.uid!!)


        }

    }
}
}


