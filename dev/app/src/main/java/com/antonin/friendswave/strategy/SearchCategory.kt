package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel

class SearchCategory: InterfaceSearch {

    override fun sortedEvent(str: String, event: List<Event>) : List<Event> {

        var tempListEvent : ArrayList<Event> = ArrayList()

        for(data in event) {

            if(data.categorie == str) {
                tempListEvent.add(data)
            }
        }
        return tempListEvent
    }

}

//class SearchByName : InterfaceSearch {
//
//    override fun sortedEvent(str: String, event: List<Event>): List<Event> {
//
//    }
//
//}
//
//class SearchByCities : InterfaceSearch {
//
//    override fun sortedEvent(str: String, event: List<Event>): List<Event> {
//        TODO("Not yet implemented")
//    }


//}