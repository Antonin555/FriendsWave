package com.antonin.friendswave.ui.chat

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.ListGeneriqueAdapter
import com.antonin.friendswave.adapter.MessageAdapter
import com.antonin.friendswave.data.firebase.FirebaseSourceEvent
import com.antonin.friendswave.data.firebase.FirebaseSourceUser
import com.antonin.friendswave.data.model.User
import com.antonin.friendswave.data.repository.EventRepo
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityGroupChatBinding
import com.antonin.friendswave.outils.AnimationLayout
import com.antonin.friendswave.ui.home.ProfilActivity
import com.antonin.friendswave.ui.viewModel.ChatVMFactory
import com.antonin.friendswave.ui.viewModel.ChatViewModel
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class GroupChatActivity : AppCompatActivity(),KodeinAware {

    override val kodein : Kodein by kodein()
    private val factory : ChatVMFactory by instance()
    private val factory2 : HomeFragmentVMFactory by instance()
    private var viewModel = ChatViewModel(repository = UserRepo(firebaseUser= FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent())
    )

    private var viewModel2  = HomeFragmentViewModel(repository = UserRepo(firebaseUser= FirebaseSourceUser()),
        repoEvent = EventRepo(firebaseEvent = FirebaseSourceEvent())
    )
    private lateinit var binding : ActivityGroupChatBinding
    var eventKey: String = ""
    var admin: String = ""
    private  lateinit var messageAdapter : MessageAdapter
    private lateinit var adapter1 : ListGeneriqueAdapter<User>
    private val animation = AnimationLayout()

    private var bool : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        //Aller chercher l'intent de l'Event
        eventKey = intent.getStringExtra("eventKey").toString()
        admin = intent.getStringExtra("admin").toString()

        viewModel = ViewModelProviders.of(this,factory).get(ChatViewModel::class.java)
        viewModel2 = ViewModelProviders.of(this,factory2).get(HomeFragmentViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat)
        binding.viewmodel = viewModel
        binding.item = viewModel2
        binding.lifecycleOwner = this
        viewModel.receiverUidGroup = admin + eventKey

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true;
        binding.chatRecyclerViewGroup.layoutManager = layoutManager

        viewModel.groupMessageList.observe(this, Observer { messageList ->
            messageAdapter = MessageAdapter(this, messageList)
            messageAdapter.addItems(messageList)
            binding.chatRecyclerViewGroup.adapter = messageAdapter
        })

        viewModel.messageGroup.observe(this, Observer { message ->
            binding.messageBoxGroup.setText(message)
        })

//        binding.teest.visibility = View.GONE

        binding.linearExpand.setOnClickListener{

            if(bool == true){
                bool = false
                animation.expand(binding.linearExpand,1000,500)
            }
            else{
                animation.collapse(binding.linearExpand,1000,30)
                bool = true
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchSpecificEvents(admin, eventKey)
        viewModel.fetchUserData()
        viewModel.fetchDiscussionGroup()


        viewModel.mainEvent.observe(this, Observer {
            viewModel.fetchParticipant(it)
        })

        adapter1 = ListGeneriqueAdapter(R.layout.recycler_contact)
        val layoutManager2 = LinearLayoutManager(this)
        binding.chatRecyclerViewGroupParticipant.layoutManager = layoutManager2
        binding.chatRecyclerViewGroupParticipant.adapter = adapter1

        viewModel.participantList.observe(this, Observer {
            adapter1.addItems(it)
        })


        adapter1.setOnListItemViewClickListener(object : ListGeneriqueAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {

                val userChoisi = viewModel.participantList.value!!.get(position)

                if(view.id == R.id.imageProfil){
                    val intent = Intent(view.context, ProfilActivity::class.java)
                    intent.putExtra("uid", userChoisi.uid)
                    startActivity(intent)
                }
            }
        })

    }


    }

