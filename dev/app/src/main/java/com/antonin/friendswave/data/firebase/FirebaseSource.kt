package com.antonin.friendswave.data.firebase

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.Friends
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.antonin.friendswave.outils.startHomeActivity
import com.antonin.friendswave.ui.event.MesEventsActivity
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


        firebaseData.child("user").child(mainUid!!).child("friendList").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ContactFragment.contactList.clear()
                for (postSnapshot in snapshot.children){
                    val friendId = postSnapshot.key.toString() // Récupère l'ID de l'ami
                    val friendEmail = postSnapshot.value.toString() // Récupère l'e-mail de l'ami
//                    val friendEmail = friendSnapshot.child("email").value
//                    val friendName = friendSnapshot.child("name").value
                    val user = User(friendId, friendEmail) // Crée une instance de User
                    ContactFragment.contactList.add(user)
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
/////////////////////////////////////////////////////////////////////////////////////////////////////


    /// RECUPERER QUE LA FRIEND LIST : A ESSAYER ////



//    var ref = FirebaseDatabase.getInstance().getReference().child("user").child(mainUid!!).child("friendList")
//    var query : Query = ref.orderByChild("email")
//    query.addListenerForSingleValueEvent(object:  ValueEventListener {
//
//        override fun onDataChange(snapshot: DataSnapshot) {
//            for (userSnapshot in snapshot.getChildren()) {
//            Map<String, Object> userMap = (Map<String, Object>) userSnapshot.getValue();
//            Map<String, String> friendList = (Map<String, String>) userMap.get("friendList");
//
//        }
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            // gestion de l'erreur
//        }
//    })

////////////////////////////////////////////////////////////////////////////////////////////////////


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


    fun fetchUsersRequest(requestList: ArrayList<User>){

        var mainUser: User = User()
        firebaseData.child("user").child(mainUid!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    mainUser = snapshot.getValue(User::class.java)!!
                    firebaseData.child("user").addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            requestList.clear()
                            for (postSnapshot in snapshot.children){
                                val currentUser = postSnapshot.getValue(User::class.java)
                                if(mainUser.friendRequest!!.containsKey(currentUser?.uid)){
                                    if(currentUser?.uid != mainUid){
                                        requestList.add(currentUser!!)
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }





    fun fetchEventsPublic1(onResult: (List<Event>) -> Unit){

        firebaseData.child("event/eventPublic").get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val eventsList = ArrayList<Event>()
            for (snap in task.result.children) {
                if (snap.exists()) {
                    val event = snap.getValue(Event::class.java)
                    eventsList.add(event!!)

                }
            }

            onResult(eventsList)


//                    adapter1.addItems(eventsList)
            } else {
                // Handle error
            }
        }
    }

        // va chercher juste les evenements de l'utilisateur dans la partie Privée

        fun fetchEventsPublic2(onResult: (List<Event>) -> Unit) {
            firebaseData.child("event/eventPrivate").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val eventsList = ArrayList<Event>()
                    for (snap in task.result.children) {
                        if (snap.exists()) {
                            val event = snap.getValue(Event::class.java)
                            if(event!!.admin == mainUid!!)
                                eventsList.add(event!!)

                        }
                    }

                    onResult(eventsList)

                } else {

                }
            }
        }







    fun addEventUserPublic(name: String, isPublic : Boolean, nbrePersonnes:Int, uid : String) {
        val database = Firebase.database
        val myRef = database.getReference("event")
        myRef.child("eventPublic/").push().setValue(Event(name,isPublic,nbrePersonnes, uid))

    }

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int, uid: String) {
        val database = Firebase.database
        val myRef = database.getReference("event")
        myRef.child("eventPrivate/").push().setValue(Event(name,isPublic,nbrePersonnes, uid))

    }


    fun acceptRequestUpdateUser(position: Int){


        //fonctionne

//        var mainUser: User = User()
//        val userRef = firebaseData.child("user").child(mainUid!!)
//        userRef.runTransaction(object : Transaction.Handler {
//            override fun doTransaction(mutableData: MutableData): Transaction.Result {
//                val mainUserSnapshot = mutableData.getValue(User::class.java)
//                if (mainUserSnapshot == null) {
//                    return Transaction.success(mutableData)
//                }
//
//                mainUser = mainUserSnapshot
//                val friendRequest = mainUser.friendRequest
//                if (friendRequest != null && friendRequest.isNotEmpty()) {
//                    val key = friendRequest.keys.elementAt(position)
//                    val email = friendRequest[key].toString()
//
//                    mainUser.friendRequest?.remove(key)
//                    mainUser.friendList?.put(key, email)
//                }
//
//                mutableData.value = mainUser
//                return Transaction.success(mutableData)
//            }
//
//            override fun onComplete(
//                databaseError: DatabaseError?,
//                committed: Boolean,
//                dataSnapshot: DataSnapshot?
//            ) {
//                if (databaseError != null) {
//                    // la transaction a échoué
//                    // Gérer l'exception
//                } else {
//                    // la transaction a réussi
//                }
//            }
        //fonctionne


//        })




        var mainUser: User = User()
        val userRef = firebaseData.child("user").child(mainUid!!)

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userSnapshot = task.result
                if (userSnapshot.exists()) {
                    // L'utilisateur existe, vous pouvez accéder aux données
                    mainUser = userSnapshot.getValue(User::class.java)!!

                    var key = mainUser.friendRequest!!.keys?.elementAt(position)

                    var kke = mainUser.friendRequest!!

//                    kke.put()

                    var email = mainUser.friendRequest!!.get(key)
                    mainUser.friendRequest!!.remove(key)
                    mainUser.friendList!!.put(key.toString(), email.toString())

                    userSnapshot.ref.setValue(mainUser)

                    val userRef = firebaseData.child("user").child(key!!)
                    userRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userSnapshot = task.result
                            if (userSnapshot.exists()) {
                                // L'utilisateur existe, vous pouvez accéder aux données
                                val user = userSnapshot.getValue(User::class.java)
                                user!!.friendList!!.put(mainUid.toString(), mainUser.email.toString())
                                // Faire quelque chose avec l'utilisateur récupéré
                            } else {
                                // L'utilisateur n'existe pas
                            }
                        } else {
                            // La tâche a échoué
                            val exception = task.exception
                            // Gérer l'exception
                        }
                    }

                    firebaseData.child("user").addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (postSnapshot in snapshot.children){
                                val currentUser = postSnapshot.getValue(User::class.java)
                                if(currentUser!!.uid.equals(key) && !currentUser.uid.equals(mainUser.uid)){  //fuckin chatgpt
                                    currentUser.friendList!!.put(mainUser.uid.toString(), mainUser.email.toString())
                                    postSnapshot.ref.setValue(currentUser)
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                } else {
                    // L'utilisateur n'existe pas
                }
            } else {
                // La tâche a échoué
                val exception = task.exception
                // Gérer l'exception
            }
        }


//
//        firebaseData.child("user").addValueEventListener(object: ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            for (postSnapshot in snapshot.children){
//                                val currentUser = postSnapshot.getValue(User::class.java)
//                                if(currentUser!!.uid.equals(key)){
//                                    currentUser.friendList!!.put(mainUser.uid.toString(), mainUser.email.toString())
//                                    postSnapshot.ref.setValue(currentUser)
//                                }
//                            }
//                        }
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//        })


//        firebaseData.child("user").child(mainUid!!).addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    mainUser = snapshot.getValue(User::class.java)!!
//
//                    var key = mainUser.friendRequest!!.keys?.elementAt(position)
//
//                    var email = mainUser.friendRequest!!.get(key)
//
//                    mainUser.friendList!!.put(key.toString(), email.toString())
//                    mainUser.friendRequest!!.remove(key)
//
//                    snapshot.ref.setValue(mainUser)
//
////                    firebaseData.child("user").addValueEventListener(object: ValueEventListener {
////                        override fun onDataChange(snapshot: DataSnapshot) {
////                            for (postSnapshot in snapshot.children){
////                                val currentUser = postSnapshot.getValue(User::class.java)
////                                if(currentUser!!.uid.equals(key)){
////                                    currentUser.friendList!!.put(mainUser.uid.toString(), mainUser.email.toString())
////                                    postSnapshot.ref.setValue(currentUser)
////                                }
////                            }
////                        }
////                        override fun onCancelled(error: DatabaseError) {
////                            TODO("Not yet implemented")
////                        }
////                    })
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })



//        firebaseData.child("user").child(mainUid!!).setValue(NotifsFragment.user)


//        firebaseData.child("user").child(key).child("friendList").child(mainUid).addValueEventListener(object : ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//            }
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })  setValue(NotifsFragment.user?.email)


//
//        firebaseData.child("user").child(mainUid!!).child("friendList").child(key).setValue(email)
//        firebaseData.child("user").child(mainUid!!).child("friendRequest").child(key).removeValue()
//        firebaseData.child("user").child(key).child("friendList").child(mainUid!!)
//            .setValue(NotifsFragment.user?.email)


        }




        //fonctionne

//        var mainUser: User = User()
//        val userRef = firebaseData.child("user").child(mainUid!!)
//        userRef.runTransaction(object : Transaction.Handler {
//            override fun doTransaction(mutableData: MutableData): Transaction.Result {
//                val mainUserSnapshot = mutableData.getValue(User::class.java)
//                if (mainUserSnapshot == null) {
//                    return Transaction.success(mutableData)
//                }
//
//                mainUser = mainUserSnapshot
//                val friendRequest = mainUser.friendRequest
//                if (friendRequest != null && friendRequest.isNotEmpty()) {
//                    val key = friendRequest.keys.elementAt(position)
//                    val email = friendRequest[key].toString()
//
//                    mainUser.friendRequest?.remove(key)
//                    mainUser.friendList?.put(key, email)
//                }
//
//                mutableData.value = mainUser
//                return Transaction.success(mutableData)
//            }
//
//            override fun onComplete(
//                databaseError: DatabaseError?,
//                committed: Boolean,
//                dataSnapshot: DataSnapshot?
//            ) {
//                if (databaseError != null) {
//                    // la transaction a échoué
//                    // Gérer l'exception
//                } else {
//                    // la transaction a réussi
//                }
//            }
//        })



        //fonctionne
//        fun acceptRequestUpdateUser1(position: Int){
//        var mainUser: User = User()
//        val userRef = firebaseData.child("user").child(mainUid!!)
//
//        userRef.addValueEventListener(object :ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                var key = mainUser.friendRequest!!.keys?.elementAt(position)
//                var email = mainUser.friendRequest!!.get(key)
//                var currentUser = snapshot.getValue(User::class.java)
//
//                if(currentUser!!.uid.equals(key) && !currentUser.uid.equals(userRef.toString())){
//
//
//                    currentUser.friendList!!.put(userRef.toString(), mainUser.email.toString())
//                    if (key != null) {
////                        mainUser.friendRequest!!.put(key, null.toString())
//                        mainUser.friendRequest!!.remove(key)
//                    }
//
//                }
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//
//        })






//        userRef.get().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val userSnapshot = task.result
//                if (userSnapshot.exists()) {
//                    // L'utilisateur existe, vous pouvez accéder aux données
//
//
//                    var key = mainUser.friendRequest!!.keys?.elementAt(position)
//
//                    var email = mainUser.friendRequest!!.get(key)
//                    mainUser.friendRequest!!.remove(key)
//                    mainUser.friendList!!.put(key.toString(), email.toString())
//
//                    userSnapshot.ref.setValue(mainUser)
//
//                    val userRef = firebaseData.child("user").child(key!!)
//                    userRef.get().addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val userSnapshot = task.result
//                            if (userSnapshot.exists()) {
//                                // L'utilisateur existe, vous pouvez accéder aux données
//                                val user = userSnapshot.getValue(User::class.java)
//                                user!!.friendList!!.put(mainUid.toString(), mainUser.email.toString())
//                                // Faire quelque chose avec l'utilisateur récupéré
//                            } else {
//                                // L'utilisateur n'existe pas
//                            }
//                        } else {
//                            // La tâche a échoué
//                            val exception = task.exception
//                            // Gérer l'exception
//                        }
//                    }
//
//                    firebaseData.child("user").addValueEventListener(object: ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            for (postSnapshot in snapshot.children){
//                                val currentUser = postSnapshot.getValue(User::class.java)
//                                if(currentUser!!.uid.equals(key) && !currentUser.uid.equals(mainUser.uid)){  //fuckin chatgpt
//                                    currentUser.friendList!!.put(mainUser.uid.toString(), mainUser.email.toString())
//                                    postSnapshot.ref.setValue(currentUser)
//                                }
//                            }
//                        }
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//                    })
//
//                } else {
//                    // L'utilisateur n'existe pas
//                }
//            } else {
//                // La tâche a échoué
//                val exception = task.exception
//                // Gérer l'exception
//            }
//        }



    fun acceptRequestUpdateUser1(position: Int){
        val userRef = firebaseData.child("user").child(mainUid!!)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mainUser = snapshot.getValue(User::class.java)
                val key = mainUser!!.friendRequest!!.keys.elementAt(position)

                val updateFriendListTransaction = object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val currentUser = mutableData.getValue(User::class.java)
                        if (currentUser == null) {
                            return Transaction.success(mutableData)
                        }

                        if (currentUser.uid == key) {
                            // Supprimer l'invitation de la liste d'amis
                            currentUser.friendRequest!!.remove(key)
                        } else {
                            // Ajouter l'invitation à la liste d'amis
                            currentUser.friendList!!.put(key, mainUser.friendRequest!![key]!!)
                            currentUser.friendRequest!!.remove(key)
                        }

                        mutableData.value = currentUser
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        if (error != null) {
                            // Gérer les erreurs ici
                        }
                    }
                }

                userRef.runTransaction(updateFriendListTransaction)
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer les erreurs ici
            }
        })
    }






//
//        firebaseData.child("user").addValueEventListener(object: ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            for (postSnapshot in snapshot.children){
//                                val currentUser = postSnapshot.getValue(User::class.java)
//                                if(currentUser!!.uid.equals(key)){
//                                    currentUser.friendList!!.put(mainUser.uid.toString(), mainUser.email.toString())
//                                    postSnapshot.ref.setValue(currentUser)
//                                }
//                            }
//                        }
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//        })


//        firebaseData.child("user").child(mainUid!!).addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    mainUser = snapshot.getValue(User::class.java)!!
//
//                    var key = mainUser.friendRequest!!.keys?.elementAt(position)
//
//                    var email = mainUser.friendRequest!!.get(key)
//
//                    mainUser.friendList!!.put(key.toString(), email.toString())
//                    mainUser.friendRequest!!.remove(key)
//
//                    snapshot.ref.setValue(mainUser)
//
////                    firebaseData.child("user").addValueEventListener(object: ValueEventListener {
////                        override fun onDataChange(snapshot: DataSnapshot) {
////                            for (postSnapshot in snapshot.children){
////                                val currentUser = postSnapshot.getValue(User::class.java)
////                                if(currentUser!!.uid.equals(key)){
////                                    currentUser.friendList!!.put(mainUser.uid.toString(), mainUser.email.toString())
////                                    postSnapshot.ref.setValue(currentUser)
////                                }
////                            }
////                        }
////                        override fun onCancelled(error: DatabaseError) {
////                            TODO("Not yet implemented")
////                        }
////                    })
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })



//        firebaseData.child("user").child(mainUid!!).setValue(NotifsFragment.user)


//        firebaseData.child("user").child(key).child("friendList").child(mainUid).addValueEventListener(object : ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//            }
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })  setValue(NotifsFragment.user?.email)


//
//        firebaseData.child("user").child(mainUid!!).child("friendList").child(key).setValue(email)
//        firebaseData.child("user").child(mainUid!!).child("friendRequest").child(key).removeValue()
//        firebaseData.child("user").child(key).child("friendList").child(mainUid!!)
//            .setValue(NotifsFragment.user?.email)


//    }

    fun acceptRequests(key: String, email: String){
//        firebaseData.child("user").child(key).child("friendList").child(mainUid!!)
//        firebaseData.child("user").child(mainUid!!).setValue(NotifsFragment.user)




    }

    fun refuseRequest(position: Int){

//        firebaseData.child("user").child(mainUid!!).setValue(NotifsFragment.user)


    }






}


