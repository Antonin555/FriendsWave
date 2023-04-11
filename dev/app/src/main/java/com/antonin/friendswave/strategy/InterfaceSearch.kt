package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event

interface InterfaceSearch {

    fun sortedEvent(str : String, event: List<Event>?): List<Event>
    fun sortedEventAroundMe(str : String, event: List<Event>?, userLatitude:String, userLongitude:String, radius : Int): List<Event>


}