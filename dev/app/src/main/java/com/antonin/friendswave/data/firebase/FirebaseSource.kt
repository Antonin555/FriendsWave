package com.antonin.friendswave.data.firebase
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.antonin.friendswave.ui.fragmentMain.ContactFragment
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
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
                        val mainUser = snapshot.getValue(User::class.java)!!
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








    //////////////////////// EVENTS  REQUETES /////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////


    // Envoyer une demande d'invitaion pour un event Privée :

    fun sendAnInvitationPrivateEvent(email: String,position: Int) {

        var eventId : String?

        firebaseData.child("event/eventPrivate").child(mainUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    var i = 0
                    for (childSnapshot in dataSnapshot.children) {
                        if(position == i){
                            eventId = childSnapshot.key
                            firebaseData.child("event/eventPrivate").child(mainUid).child(eventId!!)
                                .child("invitations").child(email.hashCode().toString())
                                .setValue(email)
                            addInvitationToUser(eventId.toString(),email)
                        }

                        i++

                    }
                } else {
                    println("Aucun événement trouvé avec ce nom")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Erreur lors de la récupération de l'ID de l'événement : ${databaseError.message}")
            }
        })

    }

    // COMPLEMENT DE LA METHODE PRECEDENTE :

    fun addInvitationToUser(eventId:String, email:String){

        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val user = childSnapshot.getValue(User::class.java)
                        if(user!!.email == email){
                            val userId = childSnapshot.key
                            val childUpdates = HashMap<String, Any>()
                            childUpdates["/user/$userId/invitations/$eventId"] = mainUid!!

                            firebaseData.updateChildren(childUpdates).addOnSuccessListener {

                            }.addOnFailureListener {
                                // Une erreur s'est produite lors de l'ajout de la nouvelle invitation
                            }

                        }else {
                            println("Aucun utilisateur trouvé avec cette adresse e-mail")
                        }
                    }
                }
        }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }






/////////////////////////////////////////////////////////////////////////////////////////////




    fun fetchInvitationEvents(eventList:ArrayList<Event>) {

        
        var eventId: Any? = ""
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()


        firebaseData.child("user").child(mainUid!!).child("invitations").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
//                    val user = dataSnapshot.getValue(User::class.java)
                    for (data in dataSnapshot.children){
                        eventValue = data.value.toString()
                        eventId = data.key.toString()

//                        eventValue = eventIdWithPrefix.split("=","}", "{")[1]
                        eventIdList.put(eventId.toString(),eventValue.toString())

                    }
                    print(eventIdList)
//                    addEventsToRecycler(eventIdList,eventList)
                    testEventsToRecycler(eventIdList,eventList)

                } else {
//                    onResult(eventList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer l'erreur ici
            }
        })


        }

    ///////////////////////////////////////////////////////////////////////



    fun testEventsToRecycler(eventIdList:HashMap<String,String>, eventList:ArrayList<Event>){
        for(i in eventIdList){
            firebaseData.child("event/eventPrivate").child(i.value.toString()).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    for (snap in task.result.children) {
                        if (snap.exists()) {
                            val event = snap.getValue(Event::class.java)
                            if(i.key == snap.key){
                                eventList.add(event!!)

                            }


                        }
                    }




//                    adapter1.addItems(eventsList)
                } else {
                    // Handle error
                }
            }
        }
        }


    fun addEventsToRecycler(eventIdList:HashMap<String,String>, eventList:ArrayList<Event>){

        for(i in eventIdList){
            firebaseData.child("event/eventPrivate").child(i.value).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (childSnapshot in dataSnapshot.children) {
                        val event = childSnapshot.getValue(Event::class.java)!!
                        if(eventIdList.containsKey(i.key)){
                            eventList.add(event)

                        }


                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    // Gérer l'erreur ici
                }
            })

    }
    }




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
        val myRef = database.getReference("event/eventPublic/").push()
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


