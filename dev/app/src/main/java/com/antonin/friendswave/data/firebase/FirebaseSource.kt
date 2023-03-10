package com.antonin.friendswave.data.firebase

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.antonin.friendswave.outils.startHomeActivity
import com.antonin.friendswave.ui.fragment.ContactFragment
import com.antonin.friendswave.ui.fragment.HomeFragment
import com.google.firebase.database.*
import com.google.firebase.database.ktx.snapshots

class FirebaseSource {


    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().getReference()


    fun currentUser() = firebaseAuth.currentUser


    fun getUserName(){

        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var name : String? = ""
                name = snapshot.getValue(User::class.java)?.name
//                var user: User? = snapshot.getValue(User::class.java)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }





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



    fun addUserToDatabase(name: String, email: String, uid: String, ){

        firebaseData.child("user").child(uid).setValue(User(name,email,uid))

    }

    fun register(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {


            if (it.isSuccessful) {
                addUserToDatabase(name,email, firebaseAuth.currentUser?.uid!!)

            }
        }
    }


    fun fetchUsers(){
        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ContactFragment.contactList.clear()
                for (postSnapshot in snapshot.children){

                    val user = postSnapshot.getValue(User::class.java)

                    ContactFragment.contactList.add(user!!)

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}


