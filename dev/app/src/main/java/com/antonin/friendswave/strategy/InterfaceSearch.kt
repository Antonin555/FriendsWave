package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User

interface InterfaceSearch {

    fun sortedEvent(str : String, event: List<Event>?, user: User): List<Event>

}