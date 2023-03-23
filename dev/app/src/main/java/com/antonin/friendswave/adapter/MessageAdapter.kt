package com.antonin.friendswave.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.antonin.friendswave.R
import com.antonin.friendswave.data.model.Message
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class MessageAdapter(val context: Context, val messageList: List<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;
    private val items = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){
            //inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHoler(view)
        }else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHoler(view)
        }

    }

    fun addItems(items: List<Message>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]
        if(holder.javaClass == SentViewHoler::class.java){
            //do stuff for sent view holder
            val viewHoler = holder as SentViewHoler
            holder.sentMessage.text = currentMessage.message
        }
        else{
            //do stuff for receive view holder
            val viewHoler = holder as ReceiveViewHoler
            holder.receiveMessage.text = currentMessage.message
        }

    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }
        else {
            return ITEM_RECEIVE
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHoler(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceiveViewHoler(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
    }

}