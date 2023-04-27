package com.antonin.friendswave.ui.viewModel

import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.outils.AlertDialog
//import com.antonin.friendswave.strategy.SearchAgeFriend
//import com.antonin.friendswave.strategy.SearchCityFriend
import com.antonin.friendswave.strategy.SearchHobbyFriend
import com.antonin.friendswave.strategy.StrategyFriend
import com.antonin.friendswave.ui.authentification.InterfaceAuth
import javax.mail.Transport
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.Message

class ContactViewModel(private val repository: UserRepo) : ViewModel() {

    var email: String? = null
    var interfaceAuth : InterfaceAuth? = null
    val user by lazy {
        repository.currentUser()
    }

//    val searchHobbyFriend = SearchHobbyFriend()
////    val searchCityFriend = SearchCityFriend()
////    val searchAgeFriend = SearchAgeFriend()

    private lateinit var searchFriendStrategy : StrategyFriend

    private val _user = MutableLiveData<User>()
    var user_live: LiveData<User> = _user

    private val _totalUserList = MutableLiveData<List<User>>()
    val totalUserList: LiveData<List<User>> = _totalUserList

    private val _emailList = MutableLiveData<List<String>>()
    val emailList: LiveData<List<String>> = _emailList

    fun fetchEmail() {
        repository.fetchfetchEmail().observeForever{ email ->
            _emailList.value = email
        }
    }

    fun addFriendRequestToUser(view: View){
        val alertDialog: AlertDialog = AlertDialog(view.context)

        val positiveButtonClickListener = DialogInterface.OnClickListener { dialog, which ->
            // Code à exécuter si le bouton positif est cliqué
            if (which == DialogInterface.BUTTON_POSITIVE) {
                alertDialog.cancel()
            }
        }

        val negativeButtonClickListener = DialogInterface.OnClickListener { dialog, which ->
            // Code à exécuter si le bouton négatif est cliqué
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                Toast.makeText(view.context,"ok on touche a rien", Toast.LENGTH_LONG).show()
                alertDialog.cancel()
            }
        }

        if (email.isNullOrEmpty()) {
            //faire un interface pour indiquer les erreurs
            interfaceAuth?.onFailure("Please enter a valid mail")
            return
        }
        if (!emailList.value!!.contains(email!!)){
//            interfaceAuth?.onFailure("This email match no account, we are sending an invitation via email")

            alertDialog.showDialog(view.context, "Attention", "This email match no account, do you want to make an invitation via email", "yes","no", positiveButtonClickListener, negativeButtonClickListener)

            //envoyer une demande directement par couriel
            sendEmail("caron.alex18@hotmail.fr")
            return
        }
        if (user_live.value!!.friendList!!.containsValue(email)){
            interfaceAuth?.onFailure("Already in your contact")
            return
        }
// verifier qu'une demande na pas deja ete envoye
//        if(repository.requestAlreadySend(email!!).value!!){
//            interfaceAuth?.onFailure("Request already sent")
//            return
//        }

        Toast.makeText(view.context, "Demande envoyee", Toast.LENGTH_SHORT).show()
        repository.addFriendRequestToUser(email!!)
    }

    // Fonction pour envoyer un e-mail
    fun sendEmail(recipient: String) {
        val props = Properties()
        props.setProperty("mail.smtp.host", "smtp.gmail.com")
        props.setProperty("mail.smtp.port", "587")
        props.setProperty("mail.smtp.auth", "true")
        props.setProperty("mail.smtp.starttls.enable", "true")
        props.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com")

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("babinobang@gmail.com", "letsg0boy$")
            }
        })

        val message = MimeMessage(session)
        message.setFrom(InternetAddress("babinobang@gmail.com"))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient))
        message.subject = "Test Email"
        message.setText("This is a test email")

        Thread(Runnable {
            try {
                Transport.send(message)
            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }).start()
    }

    fun fetchAllUser(){
        repository.fetchAllUser().observeForever{ user ->
            _totalUserList.value = user
        }
    }

    fun fetchUserData() {
        repository.getUserData().observeForever { user ->
            _user.value = user
        }
    }



//    fun strategyByAge(): List<User>{
//        searchFriendStrategy = StrategyFriend(searchHobbyFriend)
//        return searchFriendStrategy.search(user_live.value, totalUserList.value)
//    }

}