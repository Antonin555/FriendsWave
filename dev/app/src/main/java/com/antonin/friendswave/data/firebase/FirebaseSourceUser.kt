package com.antonin.friendswave.data.firebase

import android.content.Context
import android.net.Uri
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.reactivex.Completable
import java.util.Calendar

class FirebaseSourceUser {

    var storage: FirebaseStorage = Firebase.storage

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebaseData : DatabaseReference = FirebaseDatabase.getInstance().getReference()
    val mainUid = FirebaseAuth.getInstance().currentUser?.uid

    fun currentUser() = firebaseAuth.currentUser


    open fun logout() {
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

    fun fetchAllPseudo(onResult: (List<String>) -> Unit){
        val pseudoList = ArrayList<String>()

        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children){
                    val user = postSnapshot.getValue(User::class.java)
                    pseudoList.add(user?.nickname!!)
                }
                onResult(pseudoList)
            }
            override fun onCancelled(error: DatabaseError) {
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
        firebaseData.child("user").child(firebaseAuth.currentUser!!.uid)
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



    fun  addUserToDatabase(name: String, email: String, uid: String, familyName: String,
        nickname: String, city: String, date: String){
        firebaseData.child("user").child(uid).setValue(User(name,email,uid, familyName, nickname, city, date))
    }

    // REFUSER
    fun declineRequestEvent(user:User?){
        var idEvent:String = user!!.pendingRequestEventPublic!!.get(mainUid!!).toString()
        val queryEventPublic = firebaseData.child("event/eventPublic").child(idEvent)
        val queryAcceptHostEventUser = firebaseData.child("user").child(mainUid!!)
        val queryAcceptGuestEventUser = firebaseData.child("user").child(user.uid.toString())

        queryEventPublic.child("pendingRequestEventPublic").child(user.email.hashCode().toString()).removeValue()
        queryAcceptHostEventUser.child("hostPendingRequestEventPublic").child(idEvent).removeValue()

        queryAcceptGuestEventUser.child("pendingRequestEventPublic").child(idEvent).removeValue()


    }


    fun register(name: String, email: String, password: String, familyName: String, nickname: String,
        city: String, date: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            addUserToDatabase(name,email, firebaseAuth.currentUser?.uid!!, familyName!!, nickname!!, city!!, date!!)
            if (!emitter.isDisposed) {
                if (it.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(it.exception!!)
                }
            }

        }
    }


    ////                            A REVOIR FAIT PLANTER L'APP A LA PREMIERE CONNEXION -------------> //////////////////////////////////////////
    fun fetchUsersFriend(onResult: (List<User>) -> Unit){
        val userList = ArrayList<User>()
        val mainUid = firebaseAuth.currentUser?.uid
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

    fun requestAlreadySend(email: String, onResult: (Boolean) -> Unit){
        val userRef = firebaseData.child("user/")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (email == currentUser?.email) {
                        if(currentUser.friendRequest!!.containsKey(mainUid!!)){
//                            onResult(true)
                        }
                    }
                }
//                onResult(false)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
                val error = error
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

/// A REVOIR !!!!!!!!!
    fun fetchUsersRequest(onResult: (List<User>) -> Unit){
        val mainUid = FirebaseAuth.getInstance().currentUser?.uid

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

                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {

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
                mainUser.friends = mainUser.friends?.plus(1)
                firebaseData.child("user").child(mainUid).setValue(mainUser)
                //pe prob ici
                firebaseData.child("user").child(userNotif.uid!!).child("friendList").child(mainUid).setValue(mainUser.email)
                firebaseData.child("user").child(userNotif.uid!!).child("friends").setValue(userNotif.friendList?.size?.plus(1))
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

    fun addMessagetoDatabase(
        messageEnvoye: String,
        receiverUid: String,
        user: User?,
        formattedTimestamp: String
    ){
        val messageObject = Messages(messageEnvoye, mainUid, user?.name, formattedTimestamp)
        val senderRoom = receiverUid + mainUid
        val receiverRoom = mainUid + receiverUid

        firebaseData.child("chats").child(senderRoom).child("message").push()
            .setValue(messageObject).addOnSuccessListener {
                firebaseData.child("chats").child(receiverRoom).child("message").push()
                    .setValue(messageObject)
            }
    }

    fun addMessageGrouptoDatabase(
        messageEnvoye: String,
        receiverUid: String,
        userName: String,
        formattedTimestamp: String
    ){
        val messageObject = Messages(messageEnvoye, mainUid, userName, formattedTimestamp)

        firebaseData.child("chatsGroup").child(receiverUid).child("message").push()
            .setValue(messageObject)

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

    fun fetchDiscussionGroup(receiverUid: String, onResult:(List<Messages>) -> Unit){

        firebaseData.child("chatsGroup").child(receiverUid).child("message").get().addOnCompleteListener { task ->
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

    fun fetchParticipant(event: Event?, onResult:(List<User>) -> Unit){

        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<User>()
                for (postSnapshot in snapshot.children){
                    val user = postSnapshot.getValue(User::class.java)
                    if(event!!.listInscrits.containsValue(user!!.email) || event!!.admin == user!!.uid){
                        userList.add(user)
                    }
                }
                onResult(userList)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    fun fetchEmail(onResult: (List<String>) -> Unit){
        var userRef = firebaseData.child("user/")

        userRef.addValueEventListener(object : ValueEventListener {
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

    fun fetchAllUser(onResult: (List<User>) -> Unit){
        firebaseData.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<User>()
                for (postSnapshot in snapshot.children){
                    val user = postSnapshot.getValue(User::class.java)

                    userList.add(user!!)

                }
                onResult(userList)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

     // POUR AVOIR LE PROFILS DES USERS INSCRITS DANS LES EVENTS PUBLICS :

    fun fetchUserByMail(mail:String, onResult: (User?) -> Unit){

        firebaseData.child("user").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children){
                    val user = snapshot.getValue(User::class.java)
                    if(user!!.email == mail){
                        onResult(user)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }

        })
    }

    fun registerPhoto(photo: Uri, context: Context) : String{

        var currentTime = Calendar.getInstance().timeInMillis

        var storageRef = storage.reference.child("photos/").child(mainUid!!).child(currentTime.toString())

        val path = storageRef.toString().substringAfter("photos/")

        val inputStream = context.contentResolver.openInputStream(photo)
        val uploadTask = storageRef.putStream(inputStream!!)
        uploadTask.addOnSuccessListener {
//            firebaseData.child("user/" + mainUid).child("img").setValue(path)

        }.addOnFailureListener {
            // Une erreur s'est produite lors du chargement de la photo
        }

        return path
    }


    fun registerPhotoCover(photo: Uri, context: Context) : String{

        var currentTime = Calendar.getInstance().timeInMillis

        var storageRef = storage.reference.child("photosCover/").child(mainUid!!).child(currentTime.toString())

        val path = storageRef.toString().substringAfter("photosCover/")

//        val file = Uri.fromFile(File(photo.toString()))
//
//        val photoRef = storageRef.child("photos/${file.lastPathSegment}")
//
////        val uploadTask = photoRef.putFile(file)

        val inputStream = context.contentResolver.openInputStream(photo)
        val uploadTask = storageRef.putStream(inputStream!!)
        uploadTask.addOnSuccessListener {
//            firebaseData.child("user/" + mainUid).child("img").setValue(path)

        }.addOnFailureListener {
            // Une erreur s'est produite lors du chargement de la photo
        }

        return path
    }


}