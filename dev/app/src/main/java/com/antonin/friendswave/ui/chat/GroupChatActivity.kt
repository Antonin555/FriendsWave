package com.antonin.friendswave.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityGroupChatBinding
import com.antonin.friendswave.ui.viewModel.ChatVMFactory
import com.antonin.friendswave.ui.viewModel.ChatViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class GroupChatActivity : AppCompatActivity(),KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : ChatVMFactory by instance()
    private var viewModel : ChatViewModel = ChatViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private lateinit var binding : ActivityGroupChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        //Aller chercher l'intent de l'Event

        viewModel = ViewModelProviders.of(this,factory).get(ChatViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat)
        binding.lifecycleOwner = this


    }
}