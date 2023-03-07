package com.antonin.friendswave.data.firebase

import android.content.Intent
import android.widget.Toast
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseSource {


    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().getReference()



    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                println("yo")
            }
            else
                println("yo")
        }
    }

    fun singUp(name: String, email: String, password: String){
        //logik of creating a user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //code for jumping to home

                } else {

                }
            }
    }

    fun addUserToDatabase(name: String, email: String, uid: String, ){

        firebaseData.push().setValue(User(name,email,uid))

    }
}


