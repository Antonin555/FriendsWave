package com.antonin.friendswave.ui.authentification

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivityLoginBinding
import com.antonin.friendswave.outils.startHomeActivity
import com.antonin.friendswave.outils.toastShow
import com.antonin.friendswave.ui.viewModel.AuthViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import com.antonin.friendswave.ui.viewModel.AuthViewModelFactory

class LoginActivity : AppCompatActivity(), InterfaceAuth, KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory : AuthViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.view = viewModel

        viewModel.interfaceAuth = this

        viewModel.toastMessage.observe(this) { message ->
            toastShow(this,message)
        }

    }


    override fun onSuccess() {
        startHomeActivity()
    }

    override fun onFailure(message: String) {
        toastShow(this,message)
    }

    // Permet d'accéder directement a l'app sans remettre son login :
    override fun onStart() {
        super.onStart()
        viewModel.user?.let {
            startHomeActivity()
        }
    }
}