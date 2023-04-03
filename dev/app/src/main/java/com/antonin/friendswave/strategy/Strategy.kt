package com.antonin.friendswave.strategy

class Strategy(private var interfaceSearch: InterfaceSearch) {


    fun searchByCategory() {

        interfaceSearch.sortedEvent()
    }

    fun searchByName(){

        interfaceSearch.sortedEvent()
    }

    fun searchByCities(){
        interfaceSearch.sortedEvent()
    }
}