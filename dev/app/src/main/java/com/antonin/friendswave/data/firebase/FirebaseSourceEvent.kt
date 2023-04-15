package com.antonin.friendswave.data.firebase

import android.annotation.SuppressLint
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseSourceEvent {


    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    public val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().getReference()
    val mainUid = FirebaseAuth.getInstance().currentUser?.uid
    fun currentUser() = firebaseAuth.currentUser

    fun editEvent(event: Event?){
        if(event!!.isPublic == true){
            firebaseData.child("event/eventPublic").child(event.key.toString()).setValue(event)
        }else{
            firebaseData.child("event/eventPrivate").child(mainUid!!).child(event.key.toString()).setValue(event)
        }
    }

    fun deleteEvent(event: Event?){
        if(event!!.isPublic == true){
            firebaseData.child("event/eventPublic").child(event.key.toString()).removeValue()
        }
        else{
            firebaseData.child("event/eventPrivate").child(mainUid!!).child(event.key.toString()).removeValue()
        }
        firebaseData.child("chatsGroup").child(mainUid!!+ event.key.toString()).removeValue()

    }

    fun deleteConfirmation(event:Event?){
        if(event!!.isPublic == true){

            firebaseData.child("event/eventPublic").child(event.key.toString()).child("listInscrits").child(mainUid!!).removeValue()
        }
        else{
            firebaseData.child("event/eventPrivate").child(event.admin).child(event.key.toString()).child("listInscrits").child(mainUid!!).removeValue()
        }

        firebaseData.child("user").child(mainUid!!).child("eventConfirmationList").child(event.key.toString()).removeValue()

    }

    fun deletePendingEvent(event:Event?){

        if(event!!.isPublic == true){

            firebaseData.child("event/eventPublic").child(event.key.toString()).child("pendingRequestEventPublic").child(currentUser()!!.email.hashCode().toString()).removeValue()
        } else {

            firebaseData.child("event/eventPrivate").child(event.key.toString()).child("pendingRequestEventPublic").child(currentUser()!!.email.hashCode().toString()).removeValue()
        }

        firebaseData.child("user").child(mainUid!!).child("pendingRequestEventPublic").child(event.admin).removeValue()

    }


    // GET EVENT DATA DETAIL PUBLIC PAGE EVENT FRAGMENT MAIN :
    fun getEventData(key:String,onResult: (Event?) -> Unit) {

        firebaseData.child("event/eventPublic").child(key).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(Event::class.java)!!
                onResult(data)
                    }


            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseHelper", "Error fetching data", error.toException())
                onResult(null)
            }
        })
    }

    //////////////////////// EVENTS  REQUETES /////////////////////////////////////////////////////


    // 1/ L'UTILISATEUR ENVOIE UNE DEMANDE AUPRES D'UN EVENT PUBLIC :
    fun sendRequestToParticipatePublicEvent(idEvent:String, adminEvent: String){

        firebaseData.child("event/eventPublic").child(idEvent).child("pendingRequestEventPublic").child(currentUser()!!.email.hashCode().toString()).setValue(currentUser()!!.email)
        firebaseData.child("user/"+mainUid!!).child("pendingRequestEventPublic").child(adminEvent).setValue(idEvent)
        firebaseData.child("user").child(adminEvent).child("hostPendingRequestEventPublic").child(idEvent).setValue(mainUid)

    }

    // 2/ chercher les events de l'utilisateur en attente et les mettre dans le recycler :

    fun getAllEventsPendingRequestPublic(onResult: (List<Event>) -> Unit){

        var hostId: Any?
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()
        val eventList: ArrayList<Event> = ArrayList()
        firebaseData.child("user").child(mainUid!!).child("pendingRequestEventPublic").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        eventValue = data.value.toString()
                        hostId = data.key.toString()
                        eventIdList.put(hostId.toString(),eventValue.toString())

                    }
                    addPendingEventsToRecyclerNotif(eventIdList,eventList, onResult)
//                    addEventsPublicToRecyclerNotif(eventIdList,eventList, onResult)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    // 3/ METTRE A JOUR LE RECYCLER DES EVENTS EN ATTENTE POUR L'UTILISATEUR :
    fun addPendingEventsToRecyclerNotif(eventIdList:HashMap<String,String>, eventList:ArrayList<Event>, onResult: (List<Event>) -> Unit){


        firebaseData.child("event/eventPublic").addValueEventListener(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (snap in snapshot.children) {
                        if(eventIdList.containsValue(snap.key)){
                            val event = snap.getValue(Event::class.java)
                            eventList.add(event!!)
                        }
                    }
                    onResult(eventList)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }


    // COMPLEMENT DE LA METHODE PRECEDENTE :
    fun sendAnInvitationEvent(event:Event, email:String){


        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val user = childSnapshot.getValue(User::class.java)
                        if(user!!.email == email){
                            val guestId = childSnapshot.key
                            val childUpdates = HashMap<String, Any>()
                            childUpdates["/user/$guestId/invitations/${event.key}"] = mainUid!!

                            firebaseData.updateChildren(childUpdates)

                            if(event.isPublic== true){
                                firebaseData.child("event/eventPublic").child(event.key.toString()).child("invitation").child(guestId!!).setValue(email)
                            }
                            if(event.isPublic==false){
                                firebaseData.child("event/eventPrivate").child(mainUid).child(event.key.toString()).child("invitation").child(guestId!!).setValue(email)
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


    // POUR ENVOYER DES NOTIFS de demande de participation  A L'USER QUI A FAIT UN EVENT
    fun fetchDemandeInscriptionEventPublic( onResult: (List<User>) -> Unit) {

        var eventId: Any?
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()
        val userList: ArrayList<User> = ArrayList()
        firebaseData.child("user").child(mainUid!!).child("hostPendingRequestEventPublic").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        eventValue = data.value.toString()
                        eventId = data.key.toString()
                        eventIdList.put(eventId.toString(),eventValue.toString())

                    }
                    addEventsPublicToNotifsRequest(eventIdList,userList, onResult)
//                    addEventsPublicToRecyclerNotif(eventIdList,eventList, onResult)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    // POUR NOTIF FRAGMENT
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


    fun fetchSpecificEvents( hostId: String, eventValue: String, onResult: (Event) -> Unit) {
        var hostId = hostId
        var eventValue = eventValue
        var event: Event = Event()

        firebaseData.child("event/eventPrivate").child(hostId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (snap in task.result.children) {
                    if (snap.exists()) {
                        if(eventValue == snap.key){
                            event = snap.getValue(Event::class.java)!!
                            onResult(event)
                        }
                    }
                }

                firebaseData.child("event/eventPublic").get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (snap in task.result.children) {
                            if (snap.exists()) {
                                if(eventValue == snap.key){
                                    event = snap.getValue(Event::class.java)!!
                                    onResult(event)
                                }
                            }
                        }
                    }
                }

            }
        }


    }

    fun fetchConfirmationEvents( onResult: (List<Event>) -> Unit) {
        var hostId: Any?
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()
        val eventList: ArrayList<Event> = ArrayList()
        firebaseData.child("user").child(mainUid!!).child("eventConfirmationList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        eventValue = data.value.toString()
                        hostId = data.key.toString()
                        eventIdList.put(eventValue.toString(),hostId.toString())

                    }
//                    addEventsPrivateToRecyclerConfirm(eventIdList,eventList, onResult)
                    addEventsPrivateToRecyclerNotif(eventIdList,eventList, onResult)
                    addEventsPublicToRecyclerNotif2(eventIdList,eventList,onResult)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun addEventsPrivateToRecyclerNotif(eventIdList:HashMap<String,String>, eventList:ArrayList<Event>, onResult: (List<Event>) -> Unit){

            firebaseData.child("event/eventPrivate").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {


                    for (snap in snapshot.children) {

                        val event = snap.getValue(Event::class.java)
                        if(eventIdList.containsValue(snap.key)){
                            eventList.add(event!!)
                        }

                    }
                    onResult(eventList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })





    }

    //Alex 2eme fonction pour ne pas faire result children
    fun addEventsPublicToRecyclerNotif2(eventIdList:HashMap<String,String>, eventList:ArrayList<Event>, onResult: (List<Event>) -> Unit){

            firebaseData.child("event/eventPublic").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {


                    for (snap in snapshot.children) {

                        val event = snap.getValue(Event::class.java)
                        if(eventIdList.containsValue(snap.key)){
                            eventList.add(event!!)
                        }

                    }
                    onResult(eventList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
    }




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

    @SuppressLint("SuspiciousIndentation")
    // PRIVATE EVENT : LIST DES CONFIRMÉS
    fun fetchGuestAttenteEventPrive(key:String?, onResult: (List<User>) -> Unit){

        val listGuest : ArrayList<String> = ArrayList()

        firebaseData.child("event/eventPrivate").child(mainUid!!).child(key!!)
            .child("invitation").addValueEventListener( object :ValueEventListener {
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

    fun fetchGuestConfirmDetailEventPublic(key:String?, onResult: (List<User>) -> Unit){

        val listGuest : ArrayList<String> = ArrayList()

        firebaseData.child("event/eventPublic").child(key!!)
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

    // ON VA CHERCHER LES INVITATIONS EVENT PUBLIC
    @SuppressLint("SuspiciousIndentation")
    fun fetchGuestDetailEventPublic(key:String?, onResult: (List<User>) -> Unit){

        val listGuest : ArrayList<String> = ArrayList()

        firebaseData.child("event/eventPublic").child(key!!)
            .child("invitation").addValueEventListener( object :ValueEventListener {
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

            firebaseData.child("event/eventPublic").addValueEventListener( object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for (snap in snapshot.children) {
                            if (snap.exists()) {
                                val event = snap.getValue(Event::class.java)
                                if(eventIdList.containsKey(snap.key)){
                                    eventList.add(event!!)
                                }
                            }
                        }


                    }
                    onResult(eventList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })





        }


    fun addEventsPublicToNotifsRequest(eventIdList:HashMap<String,String>, userList:ArrayList<User>, onResult: (List<User>) -> Unit){

        firebaseData.child("user/").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (data in snapshot.children){
                        val user = data.getValue(User::class.java)
                        if(user!!.pendingRequestEventPublic!!.containsKey(mainUid!!)){
                            if(eventIdList.containsKey(user.pendingRequestEventPublic!!.getValue(mainUid))){
                                userList.add(user)
                            }
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        onResult(userList)
    }

    fun acceptRequestEvent(user: User?){
        var hashmail :String = user!!.email.hashCode().toString()
        var idEvent:String = user.pendingRequestEventPublic!!.get(mainUid!!).toString()
//        user!!.pendingRequestEventPublic!!.get(user.email.hashCode().toString()).toString()
        val queryEventPublic = firebaseData.child("event/eventPublic").child(idEvent)
        val queryAcceptHostEventUser = firebaseData.child("user").child(mainUid)
        val queryAcceptGuestEventUser = firebaseData.child("user").child(user.uid.toString())

        queryEventPublic.child("pendingRequestEventPublic").child(user.email.hashCode().toString()).removeValue()
        queryEventPublic.child("listInscrits").child(user.email.hashCode().toString()).setValue(user.email)

        queryAcceptHostEventUser.child("hostPendingRequestEventPublic").child(idEvent).removeValue()
        queryAcceptHostEventUser.child("ConfirmHostRequestEventPublic").child(idEvent).setValue(user.email)

        queryAcceptGuestEventUser.child("pendingRequestEventPublic").child(mainUid).removeValue()
        queryAcceptGuestEventUser.child("eventConfirmationList").child(mainUid).setValue(idEvent)

    }

    // REFUSER


    fun declineRequestEvent(user: User?){
        var idEvent:String = user!!.pendingRequestEventPublic!!.get(mainUid!!).toString()
        val queryEventPublic = firebaseData.child("event/eventPublic").child(idEvent)
        val queryAcceptHostEventUser = firebaseData.child("user").child(mainUid!!)
        val queryAcceptGuestEventUser = firebaseData.child("user").child(user.uid.toString())

        queryEventPublic.child("pendingRequestEventPublic").child(user.email.hashCode().toString()).removeValue()
        queryAcceptHostEventUser.child("hostPendingRequestEventPublic").child(idEvent).removeValue()

        queryAcceptGuestEventUser.child("pendingRequestEventPublic").child(idEvent).removeValue()


    }


    fun acceptInvitationEvent(event: Event?){

        val queryEventPrivate = firebaseData.child("event/eventPrivate").child(event!!.admin).child(event.key!!)
        val queryEventPublic = firebaseData.child("event/eventPublic").child(event!!.key.toString())
        val queryAcceptEventUser = firebaseData.child("user").child(mainUid!!)

        if(event.isPublic == true){


            queryEventPublic.child("invitation").child(mainUid!!).removeValue()
            queryEventPublic.child("listInscrits").child(mainUid).setValue(currentUser()!!.email.toString())

        }else {

            queryEventPrivate.child("listInscrits").child(currentUser()!!.email.hashCode().toString()).setValue(currentUser()!!.email.toString())
            queryEventPrivate.child("invitation").child(currentUser()!!.email.hashCode().toString()).removeValue()

        }

        queryAcceptEventUser.child("invitations").child(event.key!!).removeValue()
        queryAcceptEventUser.child("eventConfirmationList").child(event.key!!).setValue(event.admin)

    }

    fun refuseInvitationEvent(event: Event?){

        val queryEventPublic = firebaseData.child("event/eventPublic").child(event!!.key.toString())
        val queryEventPrivate = firebaseData.child("event/eventPrivate").child(event!!.admin).child(event.key!!)
        val queryAcceptEventUser = firebaseData.child("user").child(mainUid!!)

        if(event.isPublic == true){

            queryEventPublic.child("invitation").child(mainUid).removeValue()

        }else {
            queryEventPrivate.child("invitation").child(currentUser()!!.email.hashCode().toString()).removeValue()

        }

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


    // ON RAMENE JUSTE LEVENT en DETAIL pour MY EVENT
    fun fetchDetailEventPublicUser(key: String?, onResult: (Event?) -> Unit) {

        firebaseData.child("event/eventPublic/").child(key!!).addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val event = snapshot.getValue(Event::class.java)
                onResult(event!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    fun addEventUserPublic(name: String, isPublic : Boolean, nbrePersonnes:Int, uid : String,
                           category:String, date : String, horaire:String, adress:String,
                           description:String, longitude:String,latitude:String )

    {
        val database = Firebase.database
        val myRef = database.getReference("event/eventPublic/").push()
        myRef.setValue(Event(myRef.key,name,isPublic,nbrePersonnes, uid, category, date, horaire,
            adress, description, longitude, latitude))

    }

    fun addEventUserPrivate(name: String, isPublic : Boolean, nbrePersonnes:Int, uid: String,
                            category:String, date : String, horaire:String, adress:String,
                            description: String,longitude:String,latitude:String)
    {
        val database = Firebase.database
        val myRef = database.getReference ("event/eventPrivate/" + mainUid!!).push()
        myRef.setValue(Event(myRef.key, name,isPublic,nbrePersonnes, uid, category, date, horaire,
            adress,description, longitude, latitude))

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