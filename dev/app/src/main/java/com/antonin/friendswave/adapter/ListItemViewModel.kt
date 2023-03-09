package com.antonin.friendswave.adapter

abstract class ListItemViewModel{
    var adapterPosition: Int = -1
    var onListItemViewClickListener: ListGeneriqueAdapter.OnListItemViewClickListener? = null
}