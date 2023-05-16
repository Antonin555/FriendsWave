package com.antonin.friendswave.ui.viewModel

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.outils.AlertDialog
import com.antonin.friendswave.ui.chat.GroupChatActivity
import com.antonin.friendswave.ui.event.*
import com.antonin.friendswave.ui.home.ManageHomeActivity
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.antonin.friendswave.outils.sendEmail
import java.util.*


class EventFragmentViewModel(private val repository:UserRepo,private val repoEvent:EventRepo):ViewModel() {

    var name: String? = ""
    var description: String? = ""
    private var isPublic : Boolean? = false
    var photo: Uri? = null
    var nbrePersonnes : Int? = 0
    var categorie: String? = ""
    var adress: String? = ""
    var lattitude: String? = ""
    var longitude : String?  = ""
    var date: String? = ""
    var horaire: String? = ""
    var day: Int? = 0
    var month: Int? = 0
    var year:Int? = 0
    var hour:Int? = 0
    var minute: Int? = 0
    var timeStamp : Double = 0.0
    var email: String? = ""
    var pseudo:String? = ""
    var keyEvent: String? = ""

    var strCategory = MutableLiveData<String>()

//    val currentDate = formatter.parse(dateFormat.toString())

    val user by lazy {
        repository.currentUser()
    }

    private val _user = MutableLiveData<User>()
    var user_live: LiveData<User> = _user

    private val _admin = MutableLiveData<User>()
    var admin_live: LiveData<User> = _admin

    private val _guestList = MutableLiveData<List<User>>()
    val guestList: LiveData<List<User>> = _guestList

    private val _guestListPublic = MutableLiveData<List<User>>()
    val guestListPublic: LiveData<List<User>> = _guestListPublic

    private val _confirm_guestListPublic = MutableLiveData<List<User>>()
    val confirm_guestListPublic: LiveData<List<User>> = _confirm_guestListPublic

    var interfaceEvent: InterfaceEvent? = null

    private val _eventDataPrivate = MutableLiveData<Event>()
    val eventDataPrivate: LiveData<Event> = _eventDataPrivate

    private val _eventPublicUser = MutableLiveData<Event>()
    val eventPublicUser: LiveData<Event> = _eventPublicUser

    private val _eventDataPublic = MutableLiveData<Event>()
    val eventDataPublic: LiveData<Event> = _eventDataPublic

    private val _eventPendingPublic = MutableLiveData<List<Event>>()
    val eventPendingPublic: LiveData<List<Event>> = _eventPendingPublic

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList

    private val _eventListPublicUser = MutableLiveData<List<Event>>()
    val eventListPublicUser : LiveData<List<Event>> = _eventListPublicUser

    private val _eventListConfirm = MutableLiveData<List<Event>>()
    val eventListConfirm: LiveData<List<Event>> = _eventListConfirm

    private val _userListAttentePrivate = MutableLiveData<List<User>>()
    val userListAttentePrivate: LiveData<List<User>> = _userListAttentePrivate


    private val _emailList = MutableLiveData<List<String>>()
    val emailList: LiveData<List<String>> = _emailList

    fun fetchEmail() {
        repository.fetchfetchEmail().observeForever{ email ->
            _emailList.value = email
        }
    }

    fun fetchUserData() {
        repository.getUserData().observeForever { user ->
            _user.value = user
        }
    }

    fun fetchDataEvent(key: String) {
        repoEvent.getEventData(key).observeForever { event ->
            _eventDataPrivate.value = event
        }
    }

    fun goToAddEvent(view: View){
        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, AddEventActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun sendRequestToParticipatePublicEvent(idEvent:String, adminEvent:String){

        // Si user est different de l'admin de l'event, il peut faire une demande :

        if(adminEvent != user!!.uid){

            repoEvent.sendRequestToParticipatePublicEvent(idEvent,adminEvent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addEventUser(view: View) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateFormat = LocalDate.parse(date, formatter)

        if(!name.isNullOrEmpty() && nbrePersonnes!! != 0  && !user!!.uid.isEmpty() &&
            !categorie!!.isEmpty() && !date!!.isEmpty() && !horaire!!.isEmpty()
            && !adress!!.isEmpty() && !description!!.isEmpty()){
            if(photo == null)Toast.makeText(view.context,"Veuillez inserer une photo", Toast.LENGTH_LONG).show()
            if(nbrePersonnes!! > 10)Toast.makeText(view.context,"Il ne peut pas y avoir plus de 10 personnes a un event", Toast.LENGTH_LONG).show()
            if(dateFormat.isBefore(LocalDate.now()))Toast.makeText(view.context,"La date doit etre antérieure a celle d'aujoud'hui", Toast.LENGTH_LONG).show()
            else{
                if(isPublic == true) {
                    repoEvent.addEventUserPublic(name!!, isPublic!!,nbrePersonnes!!, user!!.uid, categorie!!, date!!, horaire!!, adress!!,description!!,
                        longitude!!,lattitude!!,photo!!,view.context, user!!.displayName.toString(), timeStamp )

                } else repoEvent.addEventUserPrivate(name!!, isPublic=false, nbrePersonnes!!, user!!.uid, categorie!!,date!!, horaire!!, adress!!, description!!, longitude!!,lattitude!!, photo!!, view.context, user!!.displayName.toString(), timeStamp)
                Toast.makeText(view.context,"Evenement en cours de publication", Toast.LENGTH_LONG).show()
                Intent(view.context, ManageHomeActivity::class.java).also { view.context.startActivity(it)}
            }
        } else Toast.makeText(view.context,"Veuillez remplir tous les champs", Toast.LENGTH_LONG).show()

    }


    val isChecked: MutableLiveData<Boolean> = MutableLiveData()
    val isSubscribe: MutableLiveData<Boolean> = MutableLiveData()

    fun executeOnStatusChanged(switch: CompoundButton, isChecked: Boolean) {
        isPublic = isChecked
    }

    fun executeOnSubscribeChanged(switch: CompoundButton,isSubscribe : Boolean) {
        if(isSubscribe) FirebaseMessaging.getInstance().subscribeToTopic("nom-du-topic")
        else FirebaseMessaging.getInstance().unsubscribeFromTopic("nom-du-topic")

    }

    // pour recuperer la valeur de la categorie dans le spinner :
    fun onSelectItem(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
       categorie = parent!!.adapter.getItem(pos).toString()
    }

    // ViewModel pour MyEvent :

    fun changeDate(view: View,year: Int, month: Int, day: Int) {

        val dayString =  if (day < 10) "0$day" else day.toString()
        val monthString = if (month <= 10) "0${month + 1}" else "${month + 1}"

        date = dayString + "/"+monthString +"/"+ year.toString()

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var strDate: Date? = null
        strDate = sdf.parse(date)
        timeStamp = strDate.time.toDouble()

        if (Date().after(strDate)) {
            Toast.makeText(view.context,"Impossible de remonter dans le temps",Toast.LENGTH_SHORT).show()

        }

    }

    fun changeHour(hour:Int, minute:Int) {

        val hourString = if (hour < 10) "0$hour" else hour.toString()
        val minuteString = if (minute < 10) "0$minute" else minute.toString()
        horaire = hourString + ":" + minuteString
    }

    fun fetchGuestDetailEvent(key:String?){
        repoEvent.fetchGuestDetailEvent(key).observeForever{ event ->
            _guestList.value = event
        }

    }

    fun fetchGuestAttenteEventPrive(key:String?){
        repoEvent.fetchGuestAttenteEventPrive(key).observeForever{ event ->
            _userListAttentePrivate.value = event
        }
    }

    fun fetchGuestConfirmDetailEventPublic(key: String?){

        repoEvent.fetchGuestConfirmDetailEventPublic(key).observeForever{ user ->

            _confirm_guestListPublic.value = user
        }
    }

    fun fetchAdmin(uid: String){

        repository.fetchAdmin(uid).observeForever { user ->
            _admin.value = user
        }
    }

    fun getAllEventsPendingRequestPublic(){

        repoEvent.getAllEventsPendingRequestPublic().observeForever{ event ->
            _eventPendingPublic.value= event
        }
    }

    fun fetchGuestDetailEventPublic(key:String?){
        repoEvent.fetchGuestDetailEventPublic(key).observeForever{ event->
            _guestListPublic.value = event
        }

    }

    fun fetchEventsPublic1() {
        repoEvent.fetchEventsPublic1().observeForever{ event ->
        _eventList.value = event
        }
    }

    fun fetchEventsPrivateUser() {
        repoEvent.fetchEventsPrivateUser().observeForever{ event ->
            _eventList.value = event
        }

    }


    fun fetchEventsPublicUser() {
        repoEvent.fetchEventsPublicUser().observeForever{ event ->
            _eventListPublicUser.value = event
        }

    }

    fun fetchConfirmationEvents() {

        repoEvent.fetchConfirmationEvents().observeForever{event ->
            _eventListConfirm.value = event
        }

    }

    fun fetchEventUserPrivate(pos: Int) {
        repoEvent.fetchEventUser(pos).observeForever{ event ->
            _eventDataPublic.value = event
        }
    }


    fun fetchDetailEventPublicUser(key:String?) {
        repoEvent.fetchDetailEventPublicUser(key).observeForever{ event->
            _eventDataPublic.value = event

        }
    }

    fun sendAnInvitationEvent(view:View, key: String){

        val alertDialog = AlertDialog(view.context)

        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

        val positiveButtonClickListener = DialogInterface.OnClickListener { dialog, which ->
            // Code à exécuter si le bouton positif est cliqué
            if (which == DialogInterface.BUTTON_POSITIVE) {
                sendEmail(email!!, user_live.value!!.email!!,user_live.value!!.name!!)
                Toast.makeText(view.context,"demande envoye par couriel", Toast.LENGTH_SHORT).show()
                alertDialog.cancel()
            }
        }
        val negativeButtonClickListener = DialogInterface.OnClickListener { dialog, which ->
            // Code à exécuter si le bouton négatif est cliqué
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                Toast.makeText(view.context,"ok on touche a rien", Toast.LENGTH_SHORT).show()
                alertDialog.cancel()
            }
        }


        if (email.isNullOrEmpty() || !email!!.matches(emailPattern)) {
            Toast.makeText(view.context, "Mail vide ou non valide", Toast.LENGTH_SHORT).show()
            return
        }

        if(_emailList.value!!.contains(email)) {

            alertDialog.showDialog(view.context, "Attention", "This email match no account, do you want to make an invitation via email", "yes","no", positiveButtonClickListener, negativeButtonClickListener)

        }

        if(eventDataPrivate.value?.key == key){
            repoEvent.sendAnInvitationEvent(email!!, eventDataPrivate.value!!)
        }
        if(eventDataPublic.value?.key == key){
            repoEvent.sendAnInvitationEvent(email!!, eventDataPublic.value!!)
        }
        Toast.makeText(view.context,"Invitation envoyée", Toast.LENGTH_SHORT).show()
        email = ""
    }

    fun gotoMesEventsActivity(view: View) {
        Intent(view.context, ManagerFragmentEvent::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun goToGroupChat(view: View){
        Intent(view.context, GroupChatActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun editEvent(){

        val patternDate = Regex("\\d{2}/\\d{2}/\\d{4}")
        if(_eventDataPublic.value!!.date!!.matches(patternDate)){

            repoEvent.editEvent(_eventDataPublic.value)
        }
    }

    fun  deleteEvent() {
        repoEvent.deleteEvent(_eventDataPublic.value)
    }

    fun deleteConfirmation(event:Event?) {
        repoEvent.deleteConfirmation(event)
    }

    fun deleteConfirmationGuest(event:Event?,idGuest:String){

        repoEvent.deleteConfirmationGuest(event,idGuest)
    }

    fun deletePendingEvent(event: Event?){

        repoEvent.deletePendingEvent(event)
    }

    fun startGroupChat(view: View, eventKey: String, admin: String){
        Intent(view.context, GroupChatActivity::class.java).also{
            it.putExtra("eventKey", eventKey)
            it.putExtra("admin", admin)
            view.context.startActivity(it)
        }
    }


}



