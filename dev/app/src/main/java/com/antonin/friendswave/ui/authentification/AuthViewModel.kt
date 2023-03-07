package com.antonin.friendswave.ui.authentification

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.repository.UserRepo

class AuthViewModel(private val repository: UserRepo) : ViewModel() {

    var name: String? = null
    var email: String? = null
    var password: String? = null

    fun login(){
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            return
        }
        else{
            repository.login(email!!, password!!)
        }
    }

    fun goToSignup(view: View){

        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, SignupActivity::class.java).also {
            view.context.startActivity(it)
        }


    }


}