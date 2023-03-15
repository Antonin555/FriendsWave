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
import com.antonin.friendswave.ui.fragment.EventFragment
import com.antonin.friendswave.ui.fragment.NotifsFragment
import com.antonin.friendswave.ui.fragment.HomeFragment
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable


class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().getReference()
    val mainUid = FirebaseAuth.getInstance().currentUser?.uid

    fun currentUser() = firebaseAuth.currentUser

    fun logout() = firebaseAuth.signOut()

    fun login(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun getUserData(onResult: (User?) -> Unit) {
        firebaseData.child("user").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    onResult(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    onResult(null)
                }
            })
    }



    fun addUserToDatabase(name: String, email: String, uid: String ){
        firebaseData.child("user").child(uid).setValue(User(name,email,uid))
    }


    fun register(name: String, email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            addUserToDatabase(name,email, firebaseAuth.currentUser?.uid!!)
            if (!emitter.isDisposed) {
                if (it.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(it.exception!!)
                }
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


    fun fetchOneEvent(onResult: (Event?) -> Unit) = {

        firebaseData.child("event/eventPublic")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val event = dataSnapshot.getValue(Event::class.java)
                    onResult(event)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    onResult(null)
                }
            })


    }




    fun fetchUsersR(){
        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                ContactFragment.contactList.clear()
                for (postSnapshot in snapshot.children){
                    val user = postSnapshot.getValue(User::class.java)
                    if (user != null) {
                        if (user.uid == firebaseAuth.currentUser?.uid!!)
                            NotifsFragment.user = user!!

                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun addFriendRequestToUser(email: String) {
        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var emailDB = ""
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (email == currentUser?.email) {
                        emailDB = currentUser?.email!!
                        //a voir si c'est la meilleur maniere de gerer les friend request
                        firebaseData.child("user").child(currentUser?.uid!!).child("friendRequest").child(firebaseAuth.currentUser?.uid!!).setValue(firebaseAuth.currentUser?.email)
//                        firebaseData.child("user").child(currentUser?.uid!!).child("request/").push().setValue(firebaseAuth.currentUser?.email)
                        return
                    }
                }
                if (emailDB == null) {
                    //envoyer une demande via email
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun fetchUsersRequest(){
        firebaseData.child("user").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                NotifsFragment.requestList.clear()


                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (NotifsFragment.user?.friendRequest != null){
                        if (currentUser != null) {
                            if(NotifsFragment.user?.friendRequest!!.containsKey(currentUser.uid)){
                                NotifsFragment.requestList.add(currentUser)
                            }
                        }
                    }
                }

//                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun fetchEventsPublic(){
        firebaseData.child("event/eventPublic").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                EventFragment.eventList.clear()
                for (postSnapshot in snapshot.children){
                    val event = postSnapshot.getValue(Event::class.java)
                    EventFragment.eventList.add(event!!)
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    fun fetchEventsPublic1(eventList: ArrayList<Event>){
        firebaseData.child("event/eventPublic").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                for (postSnapshot in snapshot.children){
                    val event = postSnapshot.getValue(Event::class.java)
                        eventList.add(event!!)
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun addEventUserPublic(name: String, isPublic : Boolean, nbrePersonnes:Int) {
        val database = Firebase.database
        val myRef = database.getReference("event")
        myRef.child("eventPublic/").push().setValue(Event(name,isPublic,nbrePersonnes))

    }

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int) {
        val database = Firebase.database
        val myRef = database.getReference("event")
        myRef.child("eventPrivate/").push().setValue(Event(name,isPublic,nbrePersonnes))

    }

    fun acceptRequest(key: String, email: String){


        if (mainUid != null) {
            firebaseData.child("user").child(mainUid).setValue(NotifsFragment.user)
        }

//        firebaseData.child("user").child(mainUid!!).child("friendList").child(key).setValue(email)

        firebaseData.child("user").child(key).child("friendList").child(mainUid!!).setValue(NotifsFragment.user?.email)


//        firebaseData.child("user").child(key).child("friendRequest").child(mainUid).ref.removeValue()

//        firebaseData.child("user").child(key).child("friendRequest").addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (postSnapshot in snapshot.children){
//                    val requete = postSnapshot.getValue(String::class.java)
//                    if(requete == mainUid!!){
//                        if (mainUid != null) {
//                            postSnapshot.ref.removeValue()
//
////                            firebaseData.child("user").child(requete).child("friendList").child(mainUid).setValue(
////                                NotifsFragment.user?.email)
////                            return
//                        }
//
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })




    }

    fun refuseRequest(position: Int){
        if (mainUid != null) {
            firebaseData.child("user").child(mainUid).setValue(NotifsFragment.user)
        }
    }

}


