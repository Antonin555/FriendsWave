package com.antonin.friendswave.ui.contact


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.databinding.ActivityAddContactBinding
import com.antonin.friendswave.ui.viewModel.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class AddContactActivity : AppCompatActivity(), KodeinAware {

    override val kodein : Kodein by kodein()
    private lateinit var viewModel: ContactViewModel
    private val factory : ContactViewModelFactory by instance()
    //d'ou sort cette variable
//    private lateinit var binding: AddContactBinding

    private lateinit var binding: ActivityAddContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_contact)
        viewModel = ViewModelProviders.of(this, factory).get(ContactViewModel::class.java)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onResume() {
        super.onResume()
        //pourquoi fetchEmail?
        binding.viewmodel?.fetchEmail()
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
////        return inflater.inflate(R.layout.fragment_contact, container, false)
//
//        // Inflate the layout for this fragment
//        binding  = DataBindingUtil.inflate(inflater, R.layout.activity_add_contact, container, false)
//        viewModel = ViewModelProviders.of(this,factory).get(ContactViewModel::class.java)
//        binding.viewmodel = viewModel
//        return binding.root
//    }



}