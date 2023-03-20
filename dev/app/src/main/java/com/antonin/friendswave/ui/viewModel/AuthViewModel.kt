package com.antonin.friendswave.ui.viewModel

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel

import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.ui.authentification.InterfaceAuth
import com.antonin.friendswave.ui.authentification.LoginActivity
import com.antonin.friendswave.ui.authentification.SignupActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthViewModel(private val repository: UserRepo) : ViewModel() {

    var name: String? = null
    var email: String? = null
    var password: String? = null

    private val disposables = CompositeDisposable()
    var interfaceAuth : InterfaceAuth? = null

    val user by lazy {
        repository.currentUser()
    }


    fun goToSignup(view: View){

        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, SignupActivity::class.java).also {
            view.context.startActivity(it)
        }


    }

    fun goToLogin(view: View){

        // .also permet d'eviter de déclarer une variable :
        Intent(view.context, LoginActivity::class.java).also {
            view.context.startActivity(it)
        }


    }

    fun login() {

        //validating email and password
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            interfaceAuth?.onFailure("Mauvais courriel ou mot de passe")
            return
        }

        //calling login from repository to perform the actual authentication
        val disposable = repository.login(email!!, password!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            interfaceAuth?.onSuccess()
        }, {
            //sending a failure callback
            interfaceAuth?.onFailure("it.message!!")

        })

        disposables.add(disposable)
    }

    //Doing same thing with signup
    fun signup() {
        if (email.isNullOrEmpty() || password.isNullOrEmpty() || name.isNullOrEmpty()) {
            interfaceAuth?.onFailure("Please input all values")
            return
        }
//        interfaceAuth?.onStarted()
        val disposable = repository.register(name!!,email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
//                dataReference.child().setValue(User(name,email,uid))
                interfaceAuth?.onSuccess()
            }, {
                interfaceAuth?.onFailure(it.message!!)
            })

        disposables.add(disposable)
    }

    //disposing the disposables
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }


}