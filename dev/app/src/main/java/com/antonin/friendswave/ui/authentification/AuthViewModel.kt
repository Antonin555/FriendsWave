package com.antonin.friendswave.ui.authentification

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.antonin.friendswave.data.repository.UserRepo

class AuthViewModel(private val repository: UserRepo) : ViewModel() {

    var name: String? = null
    var email: String? = null
    var password: String? = null

    var interfaceAuth : InterfaceAuth? = null

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

    fun signup() {
        if (email.isNullOrEmpty()  || password.isNullOrEmpty()  || name.isNullOrEmpty()) {

            interfaceAuth?.onFailure("Please input all values")
            return

        }

        repository.register(name!!,email!!, password!!)
        interfaceAuth?.onSuccess()
    }

    fun goToLogin(view: View){

        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, LoginActivity::class.java).also {
            view.context.startActivity(it)
        }


    }


}