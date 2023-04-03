package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event

class Strategy(private var interfaceSearch: InterfaceSearch) {

    fun searchByCategory(str:String, list: List<Event>) : List<Event> {
        return interfaceSearch.sortedEvent(str, list)
    }

//    fun searchByName(str:String, list:List<Event>){
//        interfaceSearch.sortedEvent(str, list)
//    }
//
//    fun searchByCities(str:String, list:List<Event>){
//        interfaceSearch.sortedEvent(str, list)
//    }

}