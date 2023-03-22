package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event

interface InterfaceSearch {

    fun sortedEvent(algo:String, event: List<Event>)
}