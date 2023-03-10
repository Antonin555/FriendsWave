package com.antonin.friendswave.ui.contact
import com.antonin.friendswave.ui.authentification.AuthViewModel


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivityAddContactBinding
import com.antonin.friendswave.databinding.ActivityLoginBinding
import com.antonin.friendswave.ui.authentification.AuthViewModelFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AddContactActivity() : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: HomeFragmentViewModel
    private val factory : HomeFragmentVMFactory by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)





    }
}