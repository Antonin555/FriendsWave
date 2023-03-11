package com.antonin.friendswave.data.firebase

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.antonin.friendswave.outils.startHomeActivity
import com.antonin.friendswave.ui.fragment.ContactFragment
import com.antonin.friendswave.ui.fragment.HomeFragment
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase

class FirebaseSource {






    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().getReference()


    fun currentUser() = firebaseAuth.currentUser




    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val userRef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)

    fun getUserName()  {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val userName = dataSnapshot.child("name").getValue(String::class.java)
                println("helllloooooooooooooooo " + userName!!)
                HomeFragment.str = userName
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

//    fun getUserName() : String{
//        val snapshot = firebaseData.child("user").child(firebaseAuth.currentUser!!.uid).child("name").get()
//        HomeFragment.str = snapshot.toString()
//        return snapshot.getoString()
////        firebaseData.child("user").child(firebaseAuth.currentUser!!.uid).child("name").addListenerForSingleValueEvent(object:ValueEventListener {
////            override fun onDataChange(snapshot: DataSnapshot) {
////                var name  = snapshot.getValue().toString()
////                HomeFragment.str = name
////            }
////
////            override fun onCancelled(error: DatabaseError) {
////                TODO("Not yet implemented")
////            }
////
////        })
//
//    }





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



    fun addUserToDatabase(name: String, email: String, uid: String ){

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




    fun addEventUser(name: String, isPublic : Boolean, nbrePersonnes:Int) {
        val database = Firebase.database
        val myRef = database.getReference("event")
        myRef.child(firebaseAuth.currentUser?.uid!!).setValue(Event(name,isPublic,nbrePersonnes))

    }



}


