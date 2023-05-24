package com.antonin.friendswave.outils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.antonin.friendswave.ui.authentification.LoginActivity
import com.antonin.friendswave.ui.home.ManageHomeActivity
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

fun Context.startHomeActivity() =
    Intent(this, ManageHomeActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)

    }

fun Context.startLoginActivity() =
    Intent(this, LoginActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun goToActivityWithoutArgs(context: Context, activityClass: Class<*>) {
    val intent = Intent(context, activityClass)
    context.startActivity(intent)
}


fun goToActivityWithArgs(context: Context?, activityClass: Class<*>, vararg arguments: Pair<String, Any>) {
    val intent = Intent(context, activityClass)
    for (argument in arguments) {
        val (clé, valeur) = argument
        when (valeur) {
            is String -> intent.putExtra(clé, valeur)
            is Int -> intent.putExtra(clé, valeur)
            is Boolean -> intent.putExtra(clé, valeur )
            // Ajoutez d'autres types de données selon vos besoins
        }
    }
    context!!.startActivity(intent)
}

fun toastShow(context: Context?,message:String) = Toast.makeText(context,message,Toast.LENGTH_SHORT).show()

val patternDate = Regex("\\d{2}/\\d{2}/\\d{4}")
val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")



fun sendEmail(recipient: String, name:String, email:String) {
    val props = Properties()
    props.setProperty("mail.smtp.host", "smtp-mail.outlook.com")
    props.setProperty("mail.smtp.port", "587")
    props.setProperty("mail.smtp.auth", "true")
    props.setProperty("mail.smtp.starttls.enable", "true")
    props.setProperty("mail.smtp.ssl.trust", "smtp-mail.outlook.com")

    val session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("FriendWaveOfficial@hotmail.com", "Friend12Wave12")
        }
    })

    val message = MimeMessage(session)
    message.setFrom(InternetAddress("FriendWaveOfficial@hotmail.com"))
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient))
    message.subject = "Venez rejoindre la communaute FriendsWave"
    message.setText("Bonjour " + email + " mieux connu(e) sous le nom de " +  name + " vous invite a rejoindre l'application FriendsWave")

    Thread(Runnable {
        try {
            Transport.send(message)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }).start()
}





