package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event


class SearchCategory: InterfaceSearch {


    override fun sortedEvent(str: String, event: List<Event>?): List<Event> {
        TODO("Not yet implemented")
    }

    override fun sortedEventAroundMe(str: String, events: List<Event>?, userLatitude: String,
        userLongitude: String, radius: Int): List<Event> {

        val foundEvents = mutableListOf<Event>()
        val earthRadius = 6371.0 // Rayon moyen de la Terre en kilomètres

        for (event in events!!) {
            val eventLatitude = event.lattitude as Double
            val eventLongitude = event.longitude as Double

            val dLat = Math.toRadians(eventLatitude - userLatitude as Double)
            val dLon = Math.toRadians(eventLongitude - userLongitude as Double)

            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(eventLatitude)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

            val distance = earthRadius * c

            if (distance <= radius) {
                foundEvents.add(event)
            }
        }

        return foundEvents
    }

}

class SearchByName : InterfaceSearch {

    override fun sortedEvent(str: String, event: List<Event>?): List<Event> {
        val tempListEvent : ArrayList<Event> = ArrayList()

        for(data in event!!) {

            if(data.name == str) {
                tempListEvent.add(data)
            }
        }
        return tempListEvent
    }

    override fun sortedEventAroundMe(str: String, event: List<Event>?, userLatitude: String,
        userLongitude: String, radius: Int): List<Event> {
        TODO("Not yet implemented")
    }

}

class SearchByCities : InterfaceSearch {

    override fun sortedEvent(str: String, event: List<Event>?): List<Event> {

        val tempListEvent : ArrayList<Event> = ArrayList()

        for(data in event!!) {

            if(data.adress == str) {
                tempListEvent.add(data)
            }
        }
        return tempListEvent
    }

    override fun sortedEventAroundMe(str: String, event: List<Event>?, userLatitude: String,
        userLongitude: String, radius: Int): List<Event> {
        TODO("Not yet implemented")
    }
}


//}