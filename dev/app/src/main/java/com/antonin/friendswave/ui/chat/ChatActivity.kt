package com.antonin.friendswave.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.adapter.MessageAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.model.Messages
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityChatBinding
import com.antonin.friendswave.ui.viewModel.ChatVMFactory
import com.antonin.friendswave.ui.viewModel.ChatViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ChatActivity : AppCompatActivity(), KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : ChatVMFactory by instance()
    private var viewModel : ChatViewModel = ChatViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private  lateinit var messageAdapter : MessageAdapter
    private lateinit var binding : ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val reveiverUid = intent.getStringExtra("uid")
        viewModel = ViewModelProviders.of(this,factory).get(ChatViewModel::class.java)
        viewModel.receiverUid = reveiverUid
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        val layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.layoutManager = layoutManager

        viewModel.messageList.observe(this, Observer { messageList ->
            messageAdapter = MessageAdapter(this, messageList)
            messageAdapter.addItems(messageList)
            binding.chatRecyclerView.adapter = messageAdapter
        })

        viewModel.message.observe(this, Observer { message ->
            binding.messageBox.setText(message)
        })

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchDiscussion()
    }
//
//    fun onSendButtonClick(view: View) {
//        viewModel.addMessagetoDatabase(view)
//        // Réinitialisation du texte de l'EditText
//        binding.messageBox.setText("")
//    }
}