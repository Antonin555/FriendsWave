package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event

class SearchCategory: InterfaceSearch {

    override fun sortedEvent() {

    }


}

class SearchByName : InterfaceSearch {


    override fun sortedEvent() {
        println("hello NAME")
    }


}


class SearchByCities : InterfaceSearch {


    override fun sortedEvent() {
        println("hello CITIES")
    }


}