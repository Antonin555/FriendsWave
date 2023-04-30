package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User

class Strategy(private var interfaceSearch: InterfaceSearch) {

    fun search(str:String, list: List<Event>?, user : User) : List<Event> {
        return interfaceSearch.sortedEvent(str, list, user)
    }

}