package com.antonin.friendswave.data.firebase
import androidx.compose.runtime.key
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.antonin.friendswave.ui.fragmentMain.ContactFragment
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
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
        firebaseData.child("user/").addValueEventListener(object : ValueEventListener {
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


//    fun getEventUserData(position: Int,onResult: (Event?) -> Unit) {
//
//        firebaseData.child("event/eventPublic").addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                var i = 0
//                for(snap in snapshot.children){
//                    if(position == i){
//                        val data = snap.getValue(Event::class.java)!!
//                        onResult(data)
//                    }
//                    i+=1
//
//                }
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseHelper", "Error fetching data", error.toException())
//                onResult(null)
//            }
//        })
//
//
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////







    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    fun getEventData(position: Int,onResult: (Event?) -> Unit) {

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
//                Log.e("FirebaseHelper", "Error fetching data", error.toException())
                onResult(null)
            }
        })


    }
/////////////////////////////////////////////////////////////////////////////////////////////////////


    /// RECUPERER QUE LA FRIEND LIST : A ESSAYER ////
    fun fetchUsersFriend(){
        firebaseData.child("user").child(mainUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var mainUser = snapshot.getValue(User::class.java)!!
                        firebaseData.child("user")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    ContactFragment.contactList.clear()
                                    for (postSnapshot in snapshot.children) {
                                        val currentUser = postSnapshot.getValue(User::class.java)
                                        if (mainUser.friendList!!.containsKey(currentUser?.uid)) {
                                            if (currentUser?.uid != mainUid) { //patch de merde
                                                ContactFragment.contactList.add(currentUser!!)
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


    fun sendAnInvitationPrivateEvent(email: String,position: Int) {

        val firebaseUser = firebaseData.child("user")

        val firebaseEvent = firebaseData.child("event/eventPrivate").child(mainUid!!)

        firebaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (email == currentUser?.email) {
                        firebaseUser.child(currentUser.uid!!).child("EventsRequestPrivate").child(mainUid!!).setValue(currentUser()!!.email)

                    }

//                    firebaseEvent.addValueEventListener( object : ValueEventListener {
//                override fun onDataChange(eventSnapshot: DataSnapshot) {
//                    var i = 0
//                    for (event in eventSnapshot.children) {
//                        if(position == i){
//                           firebaseEvent.child().child("AttendeesParticipants").child(currentUser!!.uid.toString()).setValue(true)
////                            attendeesParticipantsRef.child(currentUser!!.uid.toString()).setValue()
////                            event.child("AttendeesParticipants").child(currentUser!!.email.toString())
//                        }
//                        i +=1
//
//                    }
//                }
//                                    override fun onCancelled(databaseError: DatabaseError) {
//                                        // Gérer l'erreur
//                                    }
//                                })


                }
            }



                override fun onCancelled(databaseError: DatabaseError) {
                    // Gérer l'erreur
                }
            })


    }





    ///////////////////////////////////////////////////////////////////////





////////////////////////////////////////////////////////////////////////////////////////////////////


    fun addFriendRequestToUser(email: String) {
        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var emailDB = ""
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (email == currentUser?.email) {
                        emailDB = currentUser.email!!
                        //a voir si c'est la meilleur maniere de gerer les friend request
                        firebaseData.child("user").child(currentUser.uid!!).child("friendRequest").child(firebaseAuth.currentUser?.uid!!).setValue(firebaseAuth.currentUser?.email)
//                        firebaseData.child("user").child(currentUser?.uid!!).child("request/").push().setValue(firebaseAuth.currentUser?.email)
                        return
                    }
                }
//                if (emailDB == null) {
//                    //envoyer une demande via email
//                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }




    fun fetchUsersRequest(requestList: ArrayList<User>){

        var mainUser = User()
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


//    fun fetchEventsRequest(eventList: ArrayList<Event>){
//
//        var mainUser = User()
//        firebaseData.child("event/eventPrivate").child(mainUid!!).addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    mainUser = snapshot.getValue(User::class.java)!!
//                    firebaseData.child("user").addValueEventListener(object: ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            requestList.clear()
//                            for (postSnapshot in snapshot.children){
//                                val currentUser = postSnapshot.getValue(User::class.java)
//                                if(mainUser.friendRequest!!.containsKey(currentUser?.uid)){
//                                    if(currentUser?.uid != mainUid){
//                                        requestList.add(currentUser!!)
//                                    }
//                                }
//                            }
//                        }
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//                    })
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//
//    }




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

        fun fetchEventsPrivateUser(onResult: (List<Event>) -> Unit) {
            firebaseData.child("event/eventPrivate").child(mainUid!!).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val eventsList = ArrayList<Event>()
                    for (snap in task.result.children) {
                        if (snap.exists()) {
                            val event = snap.getValue(Event::class.java)
                            if(event!!.admin == mainUid)
                                eventsList.add(event)

                        }
                    }

                    onResult(eventsList)

                }
            }
        }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun fetchEventUser(position: Int,onResult: (Event) -> Unit) {
        firebaseData.child("event/eventPrivate").child(mainUid!!).addValueEventListener(object :ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for (snap in snapshot.children) {
                    val event = snap.getValue(Event::class.java)
                    if (snap.exists() && event!!.admin == mainUid) {
                        if(position == i){
                            onResult(event)
                        }
                    }
                    i+=1
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun addEventUserPublic(name: String, isPublic : Boolean, nbrePersonnes:Int, uid : String, category:String, date : String, horaire:String) {
        val database = Firebase.database
//        val myRef = database.getReference("event")
        val myRef = database.getReference("event/eventPublic" + mainUid!!).push()
        myRef.setValue(Event(myRef.key,name,isPublic,nbrePersonnes, uid, category, date, horaire))

    }

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int, uid: String, category:String, date : String, horaire:String) {
        val database = Firebase.database
        val myRef = database.getReference("event/eventPrivate/" + mainUid!!).push()
        myRef.setValue(Event(myRef.key, name,isPublic,nbrePersonnes, uid, category, date, horaire))

    }


    fun acceptRequestUpdateUser(position: Int){
        var key: String?
        var email: String?

        val userRef = FirebaseDatabase.getInstance().getReference("user").child(mainUid!!)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mainUser = dataSnapshot.getValue(User::class.java)
                println(mainUser)
                key = mainUser!!.friendRequest!!.keys.elementAt(position)
                email = mainUser.friendRequest!!.get(key)
                mainUser.friendRequest!!.remove(key)
                mainUser.friendList!!.put(key.toString(), email.toString())
                firebaseData.child("user").child(mainUid).setValue(mainUser)
                firebaseData.child("user").child(key!!).child("friendList").child(mainUid).setValue(mainUser.email)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gérer les erreurs éventuelles ici
            }
        })


    }


    fun refuseRequest(position: Int){

        var key: String?

        val userRef = FirebaseDatabase.getInstance().getReference("user").child(mainUid!!)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mainUser = dataSnapshot.getValue(User::class.java)
                println(mainUser)
                println(mainUser!!.friendRequest!!.isNotEmpty())
                if(mainUser.friendRequest!!.isNotEmpty()){
                    key = mainUser.friendRequest!!.keys.elementAt(position)
                    mainUser.friendRequest!!.remove(key)
                    mainUser.friendRequest!!.remove(mainUid)
                    println(mainUser)
                    firebaseData.child("user").child(mainUid).setValue(mainUser)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gérer les erreurs éventuelles ici
            }
        })
    }

    fun addMessagetoDatabase(messageEnvoye: String, receiverUid: String){
        val messageObject = Messages(messageEnvoye, mainUid)
        val senderRoom = receiverUid + mainUid
        val receiverRoom = mainUid + receiverUid

        firebaseData.child("chats").child(senderRoom).child("message").push()
            .setValue(messageObject).addOnSuccessListener {
                firebaseData.child("chats").child(receiverRoom).child("message").push()
                    .setValue(messageObject)
            }
    }

    fun fetchDiscussion(receiverUid: String, onResult:(List<Messages>) -> Unit){

        val senderRoom = receiverUid + mainUid

        firebaseData.child("chats").child(senderRoom).child("message").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val messageList = ArrayList<Messages>()
                for (snap in task.result.children) {
                    if (snap.exists()) {
                        val message = snap.getValue(Messages::class.java)
                        messageList.add(message!!)

                    }
                }
                onResult(messageList)
            }
        }
    }

    fun fetchEmail(onResult: (List<String>) -> Unit){
        firebaseData.child("user/").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val emailList = ArrayList<String>()
                for (postSnapshot in snapshot.children){
                    val user = postSnapshot.getValue(User::class.java)
                    emailList.add(user!!.email.toString())
                }
                onResult(emailList)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}


