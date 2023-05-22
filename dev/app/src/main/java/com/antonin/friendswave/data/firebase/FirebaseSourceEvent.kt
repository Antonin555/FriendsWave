package com.antonin.friendswave.data.firebase

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirebaseSourceEvent {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().reference
    val mainUid = FirebaseAuth.getInstance().currentUser?.uid
    fun currentUser() = firebaseAuth.currentUser
    val firebaseEvent = firebaseData.child("event")
    val firebaseUserCurrent = firebaseData.child("user").child(mainUid!!)

    fun editEvent(event: Event?){

        firebaseEvent.child(event!!.key.toString()).setValue(event)
    }

    fun deleteEvent(event: Event?){

        firebaseEvent.child(event!!.key.toString()).removeValue()
        firebaseData.child("chatsGroup").child(mainUid!!+ event.key.toString()).removeValue()
    }

    // Fragment SUBSCRIBE EVENTS or pending :

    fun deleteConfirmation(event:Event?){

        firebaseEvent.child(event!!.key.toString()).child("listInscrits").child(mainUid!!).removeValue()
        firebaseData.child("user").child(mainUid).child("eventConfirmationList").child(event.key.toString()).removeValue()
    }

    fun deleteConfirmationGuest(event: Event?, idGuest: String){

        firebaseEvent.child(event!!.key.toString()).child("listInscrits").child(idGuest).removeValue()
        firebaseData.child("user").child(idGuest).child("eventConfirmationList").child(event.key.toString()).removeValue()
    }

    fun deletePendingEvent(event:Event?){

        firebaseEvent.child(event!!.key.toString()).child("pendingRequestEventPublic").child(currentUser()!!.uid).removeValue()
        firebaseUserCurrent.child("pendingRequestEventPublic").child(event.key.toString()).removeValue()

    }

    // GET EVENT DATA DETAIL PUBLIC PAGE EVENT FRAGMENT MAIN :
    fun getEventData(key:String,onResult: (Event?) -> Unit) {
        firebaseEvent.child(key).addListenerForSingleValueEvent(object :
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

        firebaseEvent.child(idEvent).child("pendingRequestEventPublic").child(currentUser()!!.uid).setValue(currentUser()!!.email).addOnSuccessListener {

        }
        firebaseUserCurrent.child("pendingRequestEventPublic").child(idEvent).setValue(adminEvent)

    }

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
                            firebaseEvent.child(event.key.toString()).child("invitation").child(guestId!!).setValue(email)
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

        val userList: ArrayList<String> = ArrayList()
//        val userList: HashMap<String,Event> = HashMap()
        val tempList: ArrayList<User> = ArrayList()

        firebaseEvent.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    val event = data.getValue(Event::class.java)
                    if(event!!.admin == mainUid){
                        for (i in event.pendingRequestEventPublic ){
                            userList.add(i.key)
                        }
                    }
                }
                getUserFromUidList(userList, tempList, onResult)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getUserFromUidList(userList: ArrayList<String> , tempList: ArrayList<User> , onResult: (List<User>) -> Unit){

        firebaseData.child("user").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children) {
                    val user = snap.getValue(User::class.java)
                    if(userList.contains(user!!.uid)){
                        tempList.add(user)
                    }
                }
                onResult(tempList)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }


    fun getAllEventsPendingRequestPublic(onResult: (List<Event>) -> Unit){

        var hostId: Any?
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()
        val eventList: ArrayList<Event> = ArrayList()
        firebaseUserCurrent.child("pendingRequestEventPublic").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        hostId = data.value.toString()
                        eventValue = data.key.toString()
                        eventIdList.put(eventValue.toString(),hostId.toString())

                    }
                    searchEventsWithKey(eventIdList,eventList, onResult)
//                    addEventsPublicToRecyclerNotif(eventIdList,eventList, onResult)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // POUR NOTIF FRAGMENT HOME

    // cherche les invitations reçus :
    fun fetchInvitationEvents( onResult: (List<Event>) -> Unit) {

        var eventId: Any?
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()
        val eventList: ArrayList<Event> = ArrayList()
         firebaseUserCurrent.child("invitations").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        eventValue = data.value.toString()
                        eventId = data.key.toString()
                        eventIdList.put(eventId.toString(),eventValue.toString())

                    }
                    searchEventsWithKey(eventIdList,eventList, onResult)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    // FRAGMENT EVENT SUBSCRIBE OR IN PENDING :

    // cherche les events confirmés par l'admin de l'event :
    fun fetchConfirmationEvents( onResult: (List<Event>) -> Unit) {
        var hostId: Any?
        var eventValue: Any?
        val eventIdList = HashMap<String,String>()
        val eventList: ArrayList<Event> = ArrayList()
        firebaseUserCurrent.child("eventConfirmationList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        hostId = data.value.toString()
                        eventValue = data.key.toString()
                        eventIdList.put(eventValue.toString(),hostId.toString())
                    }
                    searchEventsWithKey(eventIdList,eventList,onResult)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    // méthode commune à fetchInvitationEvents et fetchConfirmationEvents pour récupérer les events :
    fun searchEventsWithKey(eventIdList: HashMap<String, String>, eventList: ArrayList<Event>, onResult: (List<Event>) -> Unit){

        firebaseEvent.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val event = snap.getValue(Event::class.java)
                    if(eventIdList.containsKey(event!!.key)){
                        eventList.add(event)
                    }
                }
                onResult(eventList)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    fun fetchSpecificEvents( hostId: String, eventValue: String, onResult: (Event) -> Unit) {
        val eventValue = eventValue

        firebaseEvent.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                    for (snap in task.result.children) {
                        if (snap.exists()) {
                            if(eventValue == snap.key){
                                val event = snap.getValue(Event::class.java)!!
                                onResult(event)
                            }
                        }
                    }
                }
            }
    }

    // POUR MY EVENT : RECHERCHE DES PARTICIPANTS INVITATIONS PRIVATE :

    // PRIVATE EVENT : LIST DES CONFIRMÉS


    // POUR MY EVENT : RECHERCHE DES PARTICIPANTS INVITATIONS PUBLICS : ///////////////////////////////////////////////////////////////////////////////////////

    fun fetchGuestConfirmDetailEventPublic(key:String?, onResult: (List<User>) -> Unit){

        val listGuest : ArrayList<String> = ArrayList()

        firebaseEvent.child(key!!).child("listInscrits").addValueEventListener( object :ValueEventListener {
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

    // on recupere la liste des inscrits dans son event public :
    @SuppressLint("SuspiciousIndentation")
    fun fetchGuestDetailEventPublic(key:String?, onResult: (List<User>) -> Unit){

        val listGuest : ArrayList<String> = ArrayList()

        firebaseEvent.child(key!!).child("invitation").addValueEventListener( object :ValueEventListener {
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


    fun fetchPendingGuestEventPublic(key:String?, onResult: (List<User>) -> Unit){

        val listGuest : ArrayList<String> = ArrayList()

        firebaseEvent.child(key!!).child("pendingRequestEventPublic").addValueEventListener( object :ValueEventListener {
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
            }
        })
    }

    fun getKeyFromValue(map: HashMap<String, String>, value: String): String? {
        for ((key, mapValue) in map.entries) {
            if (mapValue == value) {
                return key
            }
        }
        return null // Retourne null si la valeur n'est pas trouvée
    }

//    fun acceptRequestEvent(user: User?) {
//        val idEvent: String = getKeyFromValue(user!!.pendingRequestEventPublic!!, mainUid!!).toString()
//
//        val queryEvent = firebaseEvent.child(idEvent)
//        val queryUser = firebaseData.child("user").child(user.uid.toString())
//        val queryAcceptHostEventUser = firebaseData.child("user").child(mainUid)
//
//        // Utiliser une transaction pour supprimer les nœuds "pendingRequestEventPublic"
//        firebaseEvent.runTransaction(object : Transaction.Handler {
//            override fun doTransaction(currentData: MutableData): Transaction.Result {
//                val eventNode = currentData.child(idEvent)
//                val pendingRequestNode = eventNode.child("pendingRequestEventPublic").child(user.uid!!)
//                pendingRequestNode.value = null // Supprimer le nœud "pendingRequestEventPublic" dans firebaseEvent
//                return Transaction.success(currentData)
//            }
//
//            override fun onComplete(
//                error: DatabaseError?,
//                committed: Boolean,
//                currentData: DataSnapshot?
//            ) {
//                if (error != null) {
//                    // La suppression a échoué
//                    // Gérer les erreurs
//                } else if (committed) {
//                    // La suppression est réussie dans firebaseEvent
//
//                    // Utiliser une autre transaction pour supprimer le nœud "pendingRequestEventPublic" dans firebaseData
//                    firebaseData.runTransaction(object : Transaction.Handler {
//                        override fun doTransaction(currentData: MutableData): Transaction.Result {
//                            val userNode = currentData.child("user").child(user.uid.toString())
//                            val pendingRequestNode = userNode.child("pendingRequestEventPublic").child(idEvent)
//                            pendingRequestNode.value = null // Supprimer le nœud "pendingRequestEventPublic" dans firebaseData
//                            return Transaction.success(currentData)
//                        }
//
//                        override fun onComplete(
//                            error: DatabaseError?,
//                            committed: Boolean,
//                            currentData: DataSnapshot?
//                        ) {
//                            if (error != null) {
//                                // La suppression a échoué
//                                // Gérer les erreurs
//                            } else if (committed) {
//                                // La suppression est réussie dans firebaseData
//
//                                // Effectuer les autres opérations
//                                queryEvent.child("listInscrits").child(user.uid.toString()).setValue(user.email)
//                                queryUser.child("eventConfirmationList").child(idEvent).setValue(mainUid)
//                                queryAcceptHostEventUser.child("ConfirmHostRequestEventPublic").child(idEvent).setValue(user!!.email)
//                            }
//                        }
//                    })
//                }
//            }
//        })
//    }


    fun acceptRequestEvent(user: User?){


        val idEvent: String = getKeyFromValue(user!!.pendingRequestEventPublic!!, mainUid!!).toString()
        val queryEvent = firebaseEvent.child(idEvent)

//        val queryAcceptHostEventUser = firebaseData.child("user").child(mainUid)

        queryEvent.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                queryEvent.child("pendingRequestEventPublic").child(user.uid.toString()).removeValue()
                queryEvent.child("listInscrits").child(user.uid.toString()).setValue(user.email.toString())
//                event!!.pendingRequestEventPublic!!.remove(user.uid.toString())
//                event.listInscrits.put(user.uid.toString(),user.email.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        acceptEventForUser(user, idEvent)

    }

    fun acceptEventForUser(user:User?,idEvent: String){

        val queryUser = firebaseData.child("user").child(user!!.uid.toString())
        queryUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                queryUser.child("pendingRequestEventPublic").child(idEvent).removeValue()
                queryUser.child("eventConfirmationList").child(idEvent).removeValue()


//                user_guest?.pendingRequestEventPublic!!.remove(idEvent)
//                user_guest.eventConfirmationList!!.put(idEvent, mainUid!!)
//                mainUser.friends = mainUser.friends?.plus(1)
                firebaseData.child("user").child(mainUid!!).child("ConfirmHostRequestEventPublic").child(idEvent).setValue(user!!.email)
//                firebaseData.child("user").child(mainUid).setValue(mainUser)
//                firebaseData.child("user").child(user.uid!!).child("friendList").child(mainUid).setValue(mainUser.email)
//                firebaseData.child("user").child(user.uid!!).child("friends").setValue(user.friendList?.size?.plus(1))
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })


    }




    // REFUSER
    fun declineRequestEvent(user: User?){
        val idEvent:String = user!!.pendingRequestEventPublic!!.get(mainUid!!).toString()
        val queryEventPublic = firebaseEvent.child(idEvent)
        val queryAcceptGuestEventUser = firebaseData.child("user").child(user.uid.toString())

        queryEventPublic.child("pendingRequestEventPublic").child(user.uid.toString()).removeValue()
        queryAcceptGuestEventUser.child("pendingRequestEventPublic").child(idEvent).removeValue()

    }


    fun acceptInvitationEvent(event: Event?){

        val queryEvent = firebaseEvent.child(event!!.key.toString())
        val queryAcceptEventUser = firebaseData.child("user").child(mainUid!!)



        queryEvent.child("invitation").child(mainUid).removeValue()
        queryEvent.child("listInscrits").child(mainUid).setValue(currentUser()!!.email.toString())


        queryAcceptEventUser.child("invitations").child(event.key!!).removeValue()
        queryAcceptEventUser.child("eventConfirmationList").child(event.key!!).setValue(event.admin)

    }

    fun refuseInvitationEvent(event: Event?){
        val queryEvent = firebaseEvent.child(event!!.key.toString())
        val queryAcceptEventUser = firebaseData.child("user").child(mainUid!!)

        queryEvent.child("invitation").child(mainUid).removeValue()
        queryAcceptEventUser.child("invitations").child(event.key!!).removeValue()
    }


    // Recupere les events publics dans Event Fragment :
    fun fetchEvents(onResult: (List<Event>) -> Unit){

        firebaseEvent.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventsList = ArrayList<Event>()
                for (snap in snapshot.children) {
                    val event = snap.getValue(Event::class.java)
                    if(event!!.public == true){
                        eventsList.add(event)
                    }
                }
                onResult(eventsList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    // Recupere les events Privées de l'user et les insere dans My Event Fragment :
    fun fetchEventsPrivateUser(onResult: (List<Event>) -> Unit) {
        firebaseEvent.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val eventsList = ArrayList<Event>()
                for (snap in task.result.children) {
                    if (snap.exists()) {
                        val event = snap.getValue(Event::class.java)
                        if(event!!.admin == mainUid && event.public == false)
                            eventsList.add(event)
                    }
                }
                onResult(eventsList)
            }
            else {
                task.exception?.printStackTrace()
            }
        }
    }

    // Recupere les events Publics de l'user et les insere dans My Event Fragment :
    fun fetchEventsPublicUser(onResult: (List<Event>) -> Unit) {
        firebaseEvent.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val eventsList = ArrayList<Event>()
                for (snap in task.result.children) {
                    if (snap.exists()) {
                        val event = snap.getValue(Event::class.java)
                        if(event!!.admin == mainUid && event.public == true)
                            eventsList.add(event)
                    }
                }
                onResult(eventsList)
            }
        }
    }

    // ON RAMENE JUSTE LEVENT en DETAIL pour MY EVENT
    fun fetchDetailEventPublicUser(key: String?, onResult: (Event?) -> Unit) {

        firebaseData.child("event/").child(key!!).addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val event = snapshot.getValue(Event::class.java)
                onResult(event!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addEventUser(name: String, isPublic: Boolean, nbrePersonnes:Int, uid: String, category:String, date: String, horaire:String, adress:String,
        description:String, longitude: String, latitude: String, photo: Uri, context: Context, host:String, timeStamp:Double, duree:Int)
    {
        val database = Firebase.database
        val myRef = database.getReference("event").push()
        myRef.setValue(Event(myRef.key,name,isPublic,nbrePersonnes, uid, category, date, horaire,
            adress, description, latitude, longitude, duree, host,timeStamp, nbreInscrit = 0))
        registerPhotoEvent(photo, context, myRef.key!!)

    }

    //////////////////////////////////////////Strategie
    fun fetchStrategieEvent(onResult: (List<Event>) -> Unit){
        firebaseEvent.addValueEventListener(object : ValueEventListener {
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

    fun registerPhotoEvent(photo: Uri,context:Context ,key:String) : String{

        val storage: FirebaseStorage = Firebase.storage
        val currentTime = Calendar.getInstance().timeInMillis
        val storageRef = storage.reference.child("photosEvent/").child(key).child(currentTime.toString())
        val path = storageRef.toString().substringAfter("photosEvent/")
        Firebase.database.getReference("event/" + key).child("imgEvent").setValue(path)
        val inputStream = context.contentResolver.openInputStream(photo)
        val uploadTask = storageRef.putStream(inputStream!!)
        uploadTask.addOnSuccessListener {

        }.addOnFailureListener {

        }
        return path
    }

}