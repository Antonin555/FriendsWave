package com.antonin.friendswave.data.firebase
import android.annotation.SuppressLint
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
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

    fun logout() {
        firebaseAuth.signOut()
        FirebaseDatabase.getInstance().purgeOutstandingWrites()
        FirebaseDatabase.getInstance().goOffline()
    }

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

    fun fetchInteret(onResult: (List<String>?) -> Unit) {
        firebaseData.child("interet")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val interetList = ArrayList<String>()
                    for(snap in snapshot.children){
                        val interet = snap.getValue(String::class.java)!!
                        interetList.add(interet)
                    }
                    onResult(interetList)
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }

    fun editProfil(user_live: User?) {
        firebaseData.child("user").child(mainUid!!).setValue(user_live)
    }

    fun getUserProfilData(profilUid: String?, onResult: (User?) -> Unit){
        firebaseData.child("user").child(profilUid!!)
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

    fun verifAmitier(profilUid: String?, onResult: (Boolean?) -> Unit){
        firebaseData.child("user").child(mainUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val mainUser = snapshot.getValue(User::class.java)!!
                        if (mainUser.friendList!!.containsKey(profilUid)) {
                            val ami = true
                            onResult(ami)
                        }
                        else{
                            val ami = false
                            onResult(ami)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }

    fun  addUserToDatabase(name: String, email: String, uid: String ){
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

    fun fetchUsersFriend(onResult: (List<User>) -> Unit){
        val userList = ArrayList<User>()
        firebaseData.child("user").child(mainUid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val mainUser = snapshot.getValue(User::class.java)!!
                        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
//                                val userList = ArrayList<User>()
                                for (postSnapshot in snapshot.children){
                                    val user = postSnapshot.getValue(User::class.java)
                                    if(mainUser.friendList!!.containsKey(user?.uid!!)){
                                        userList.add(user)
                                    }
                                }
                                onResult(userList)
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    //////////////////////// EVENTS  REQUETES /////////////////////////////////////////////////////

    fun sendAnInvitationPrivateEvent(email: String,position: Int) {

        var eventId : String?

        firebaseData.child("event/eventPrivate").child(mainUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    var i = 0
                    for (childSnapshot in dataSnapshot.children)  {
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



    fun fetchInvitationEvents( onResult: (List<Event>) -> Unit) {

        var eventId: Any?
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()
        val eventList: ArrayList<Event> = ArrayList()
        firebaseData.child("user").child(mainUid!!).child("invitations").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        eventValue = data.value.toString()
                        eventId = data.key.toString()
                        eventIdList.put(eventId.toString(),eventValue.toString())

                    }
                    addEventsPrivateToRecyclerNotif(eventIdList,eventList, onResult)
                    addEventsPublicToRecyclerNotif(eventIdList,eventList, onResult)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun fetchConfirmationEvents( onResult: (List<Event>) -> Unit) {

        var eventId: Any?
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()
        val eventList: ArrayList<Event> = ArrayList()
        firebaseData.child("user").child(mainUid!!).child("eventConfirmationList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        eventValue = data.value.toString()
                        eventId = data.key.toString()
                        eventIdList.put(eventId.toString(),eventValue.toString())

                    }
//                    addEventsPrivateToRecyclerConfirm(eventIdList,eventList, onResult)
                    addEventsPrivateToRecyclerNotif(eventIdList,eventList, onResult)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun addEventsPrivateToRecyclerNotif(eventIdList:HashMap<String,String>, eventList:ArrayList<Event>, onResult: (List<Event>) -> Unit){
        for(i in eventIdList){
            firebaseData.child("event/eventPrivate").child(i.value).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (snap in task.result.children) {
                        if (snap.exists()) {
                            val event = snap.getValue(Event::class.java)
                            if(i.key == snap.key){
                                eventList.add(event!!)
                            }
                        }
                    }
                    onResult(eventList)
                }
            }
        }
    }


    ///  A FAIRE :

    // list des invitations pour event PRIVATE
    // list des confirmés pour PUBLIC
    // AVOIR LE DETAIL DE L'EVENT public qu'on a crée

    // POUR MY EVENT : RECHERCHE DES PARTICIPANTS INVITATIONS PRIVATE :

    @SuppressLint("SuspiciousIndentation")

    // PRIVATE EVENT : LIST DES CONFIRMÉS
    fun fetchGuestDetailEvent(key:String?, onResult: (List<User>) -> Unit){

        val listGuest : ArrayList<String> = ArrayList()

            firebaseData.child("event/eventPrivate").child(mainUid!!).child(key!!)
                .child("listInscrits").addValueEventListener( object :ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for(data in dataSnapshot.children){
                            listGuest.add(data!!.value.toString())
                        }
                        searchGuest(listGuest, onResult)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }






    // POUR MY EVENT : RECHERCHE DES PARTICIPANTS INVITATIONS PUBLICS : ///////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("SuspiciousIndentation")
    fun fetchGuestDetailEventPublic(key:String?, onResult: (List<User>) -> Unit){

        val listGuest : ArrayList<String> = ArrayList()

        firebaseData.child("event/eventPublic").child(key!!)
            .child("invitations").addValueEventListener( object :ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for(data in dataSnapshot.children){
                            listGuest.add(data!!.value.toString())
                        }
                        searchGuest(listGuest, onResult)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    fun searchGuest(listGuest:ArrayList<String>, onResult: (List<User>) -> Unit){

        firebaseData.child("user/").addValueEventListener( object :ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val userList:ArrayList<User> = ArrayList()
                for(data in snapshot.children){
                    val user = data.getValue(User::class.java)
                    if(listGuest.contains(user!!.email)){
                        userList.add(user)
                    }
                }
                onResult(userList)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addEventsPublicToRecyclerNotif(eventIdList:HashMap<String,String>, eventList:ArrayList<Event>, onResult: (List<Event>) -> Unit){
        for(i in eventIdList){
            firebaseData.child("event/eventPublic").child(i.key).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (snap in task.result.children) {
                        if (snap.exists()) {
                            val event = snap.getValue(Event::class.java)
                            if(i.key == snap.key){
                                eventList.add(event!!)
                            }
                        }
                    }
                    onResult(eventList)
                }
            }
        }
    }

    fun acceptInvitationEvent(event: Event?){

        val queryEventPrivate = firebaseData.child("event/eventPrivate").child(event!!.admin).child(event.key!!)
        val queryAcceptEventUser = firebaseData.child("user").child(mainUid!!)
        queryEventPrivate.child("listInscrits").child(currentUser()!!.email.hashCode().toString()).setValue(currentUser()!!.email.toString())
        queryEventPrivate.child("invitations").child(currentUser()!!.email.hashCode().toString()).removeValue()
        queryAcceptEventUser.child("invitations").child(event.key!!).removeValue()
        queryAcceptEventUser.child("listInscrits").child(event.key!!).setValue(event.admin)

    }

    fun refuseInvitationEvent(event: Event?){

        val queryEventPrivate = firebaseData.child("event/eventPrivate").child(event!!.admin).child(event.key!!)
        val queryAcceptEventUser = firebaseData.child("user").child(mainUid!!)
        queryEventPrivate.child("invitations").child(currentUser()!!.email.hashCode().toString()).removeValue()
        queryAcceptEventUser.child("invitations").child(event.key!!).removeValue()

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


    fun fetchEventsPublicUser(onResult: (List<Event>) -> Unit) {
        firebaseData.child("event/eventPublic").get().addOnCompleteListener { task ->
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



    fun fetchDetailEventPublicUser(key: String?) {

        firebaseData.child("event/eventPublic/").child(key!!).addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Event::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    fun addEventUserPublic(name: String, isPublic : Boolean, nbrePersonnes:Int, uid : String, category:String, date : String, horaire:String, adress:String) {
        val database = Firebase.database
        val myRef = database.getReference("event/eventPublic/").push()
        myRef.setValue(Event(myRef.key,name,isPublic,nbrePersonnes, uid, category, date, horaire, adress))

    }

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int, uid: String, category:String, date : String, horaire:String, adress:String) {
        val database = Firebase.database
        val myRef = database.getReference("event/eventPrivate/" + mainUid!!).push()
        myRef.setValue(Event(myRef.key, name,isPublic,nbrePersonnes, uid, category, date, horaire, adress))

    }


    fun addFriendRequestToUser(email: String) {
        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (email == currentUser?.email) {
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

    fun addFriendRequestToUserByUid(uid: String?) {
        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (uid == currentUser?.uid) {
                        //a voir si c'est la meilleur maniere de gerer les friend request
                        firebaseData.child("user").child(currentUser?.uid!!).child("friendRequest").child(firebaseAuth.currentUser?.uid!!).setValue(firebaseAuth.currentUser?.email)
//                        firebaseData.child("user").child(currentUser?.uid!!).child("request/").push().setValue(firebaseAuth.currentUser?.email)
                        return
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun removeFriend(uid: String?){
        firebaseData.child("user").child(mainUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val currentUser = dataSnapshot.getValue(User::class.java)
                    currentUser?.friendList?.remove(uid)
                    firebaseData.child("user").child(mainUid).setValue(currentUser)
                    firebaseData.child("user").child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val currentUser2 = dataSnapshot.getValue(User::class.java)
                                currentUser2?.friendList?.remove(mainUid)
                                firebaseData.child("user").child(uid).setValue(currentUser2)

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

    fun sendSignalement(uid: String?, messSignalement: String?){
        val messageObject = Messages(messSignalement, mainUid)

        firebaseData.child("signalement").child(uid!!).child("message").push()
            .setValue(messageObject)
    }


    fun fetchUsersRequest(onResult: (List<User>) -> Unit){


        firebaseData.child("user").child(mainUid!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val mainUser = snapshot.getValue(User::class.java)!!
                    firebaseData.child("user").addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userList = ArrayList<User>()
                            for (postSnapshot in snapshot.children){
                                val currentUser = postSnapshot.getValue(User::class.java)
                                if(mainUser.friendRequest!!.containsKey(currentUser?.uid)){
                                    if(currentUser?.uid != mainUid){
                                        userList.add(currentUser!!)
                                    }
                                }
                            }
                            onResult(userList)
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



    fun acceptRequestUpdateUser(userNotif: User?){

        val userRef = FirebaseDatabase.getInstance().getReference("user").child(mainUid!!)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mainUser = dataSnapshot.getValue(User::class.java)
                println(mainUser)
                mainUser?.friendRequest!!.remove(userNotif?.uid)
                mainUser.friendList!!.put(userNotif?.uid!!, userNotif.email!!)
                firebaseData.child("user").child(mainUid).setValue(mainUser)
                //pe prob ici
                firebaseData.child("user").child(userNotif.uid!!).child("friendList").child(mainUid).setValue(mainUser.email)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gérer les erreurs éventuelles ici
            }
        })

    }


    fun refuseRequest(userNotif: User?){

        val userRef = FirebaseDatabase.getInstance().getReference("user").child(mainUid!!)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mainUser = dataSnapshot.getValue(User::class.java)
                if(mainUser?.friendRequest!!.isNotEmpty()){
                    mainUser.friendRequest!!.remove(userNotif?.uid!!)
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

    //////////////////////////////////////////Strategie

    fun fetchStrategieEvent(onResult: (List<Event>) -> Unit){
        firebaseData.child("event/eventPublic").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventList = ArrayList<Event>()
                for (postSnapshot in snapshot.children){
                    val event = postSnapshot.getValue(Event::class.java)

                    eventList.add(event!!)

                }
                onResult(eventList)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}


