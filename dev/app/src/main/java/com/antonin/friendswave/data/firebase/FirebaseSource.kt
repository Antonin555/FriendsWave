package com.antonin.friendswave.data.firebase

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.ui.fragment.ContactFragment
import com.antonin.friendswave.ui.fragment.EventFragment
import com.antonin.friendswave.ui.fragment.NotifsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable


class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    private val myData = MutableLiveData<Event>()


    val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().getReference()
    val mainUid = FirebaseAuth.getInstance().currentUser?.uid
    val firebaseDataEvent : DatabaseReference = FirebaseDatabase.getInstance().getReference("event")
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





    fun getEventData(position: Int,onResult: (Event?) -> Unit) {
        val eventLiveData = MutableLiveData<Event>()
        firebaseData.child("event/eventPublic").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for(snap in snapshot.children){
                    if(position == i){
                        val data = snap.getValue(Event::class.java)!!
                        onResult(data)
                    }
                    i+=1

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseHelper", "Error fetching data", error.toException())
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

            @SuppressLint("SuspiciousIndentation")
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

    fun acceptRequest(key: String, email: String) {



//        NotifsFragment.user
//        if (mainUid != null) {
//            firebaseData.child("user").child(mainUid).setValue(NotifsFragment.user)


//        }

        firebaseData.child("user").child(mainUid!!).child("friendList").child(key).setValue(email)

//        firebaseData.child("user").child(mainUid!!).child("friendRequest").child(key).removeValue{ it -> }


//        firebaseData.child("user").child(mainUid!!).child("friendRequest").removeValue(
//            DatabaseReference.CompletionListener { error, ref ->
//                println("Value was set. Error = $error")
//                // Or: throw error.toException();
//            })

//        val queryRef: Query = firebaseData.child("user").child(mainUid!!).orderByChild("friendRequest")
//
//        queryRef.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
//                println(snapshot.ref.child("friendRequest").toString())
//                firebaseData.child("user").child(snapshot.key.toString()).setValue(null);
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//                TODO("Not yet implemented")
////                firebaseData.child("user").child(mainUid).setValue(NotifsFragment.user)
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })


        firebaseData.child("user").child(key).child("friendList").child(mainUid!!).setValue(NotifsFragment.user?.email)

        deleteRequest(key)
//        queryByTime.get().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                for (ds in task.result.children) {
//                    println("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj")
//                    queryByTime.removeValue()
//
//                }
//            } else {
//                task.exception!!.message?.let { Log.d("TAG", it) } //Never ignore potential errors!
//            }
//        }



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

    fun deleteRequest(key: String) {


        val db = FirebaseDatabase.getInstance().reference
        val queryByTime = db.child("user").child(mainUid!!).child("friendRequest").equalTo(key)
        queryByTime.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (ds in task.result.children) {
                    print(task.result.value)
                    ds.ref.removeValue()
                    println("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj")
                }
            } else {
                task.exception!!.message?.let { Log.d("TAG", it) } //Never ignore potential errors!
            }
        }
    }

}


