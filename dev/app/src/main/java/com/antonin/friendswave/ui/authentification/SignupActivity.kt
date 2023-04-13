package com.antonin.friendswave.ui.authentification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil

import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivitySingupBinding
import com.antonin.friendswave.outils.startHomeActivity

import com.antonin.friendswave.ui.viewModel.AuthViewModel
import com.antonin.friendswave.ui.viewModel.AuthViewModelFactory

import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class SignupActivity : AppCompatActivity(), InterfaceAuth, KodeinAware {

    //    private lateinit var progressbar : ProgressBar
    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

//        progressbar = findViewById(R.id.progressbar)
        val binding: ActivitySingupBinding = DataBindingUtil.setContentView(this, R.layout.activity_singup)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.interfaceAuth = this

        viewModel.fetchAllPseudo()

    }


    override fun onSuccess() {
        startHomeActivity()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    override fun onStarted() {
////        progressbar.visibility = View.VISIBLE
//        Intent(this, HomeActivity::class.java).also {
//            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(it)
//        }
//    }
////
//    override fun onSuccess() {
////        progressbar.visibility = View.GONE
//        startHomeActivity()
//    }


}