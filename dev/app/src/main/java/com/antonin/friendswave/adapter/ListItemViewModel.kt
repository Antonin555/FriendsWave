package com.antonin.friendswave.adapter

import android.net.Uri

abstract class ListItemViewModel{
    var adapterPosition: Int = -1
    var onListItemViewClickListener: ListGeneriqueAdapter.OnListItemViewClickListener? = null
    var img : String?= ""
    var imgEvent : String? = ""
    var imgCover : String? = ""
    var lastMessage: HashMap<String, String>? = HashMap()

}