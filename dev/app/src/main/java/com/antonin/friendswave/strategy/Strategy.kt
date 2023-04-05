package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event

class Strategy(private var interfaceSearch: InterfaceSearch) {

    fun search(str:String, list: List<Event>?) : List<Event> {
        return interfaceSearch.sortedEvent(str, list)
    }

}