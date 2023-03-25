package com.antonin.friendswave.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.data.model.Messages
import javax.mail.Transport
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.Message

class ContactViewModel(private val repository: UserRepo) : ViewModel() {

    var email: String? = null

//    val user by lazy {
//        repository.currentUser()
//    }

    private val _emailList = MutableLiveData<List<String>>()
    val emailList: LiveData<List<String>> = _emailList

    fun fetchEmail() {
        repository.fetchfetchEmail().observeForever{ email ->
            _emailList.value = email
        }
    }

    fun addFriendRequestToUser(){

        if (email.isNullOrEmpty()) {
            //faire un interface pour indiquer les erreurs
//            interfaceAuth?.onFailure("Please enter a valid mail and a valid password")
            return
        }
        if (!emailList.value!!.contains(email!!)){
            //envoyer une demande directement par couriel
            sendEmail("caron.alex18@hotmail.fr")
            return
        }
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

}