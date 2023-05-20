package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.Event
import com.antonin.friendswave.data.model.User


class SearchCategory: InterfaceSearch {


    override fun sortedEvent(str: String, event: List<Event>?, user: User): List<Event> {
        val tempListEvent : ArrayList<Event> = ArrayList()

        for(data in event!!) {
            if(str.contains(data.categorie!!) || data.categorie!!.contains(str)) {
                tempListEvent.add(data)
            }
        }
        return tempListEvent
    }

}

class SearchByName : InterfaceSearch {

    override fun sortedEvent(str: String, event: List<Event>?, user: User): List<Event> {
        val tempListEvent : ArrayList<Event> = ArrayList()

        for(data in event!!) {

            if(data.name!!.contains(str) || data.description!!.contains(str)) {
                tempListEvent.add(data)
            }
        }
        return tempListEvent
    }

}

class SearchByCities : InterfaceSearch {

    override fun sortedEvent(str: String, events: List<Event>?, user: User): List<Event> {

        val foundEvents = mutableListOf<Event>()
        val earthRadius = 6371.0 // Rayon moyen de la Terre en kilomètres

        val radius = str.toInt()

        for (event in events!!) {
            val eventLatitude = event.lattitude!!.toDouble()
            val eventLongitude = event.longitude!!.toDouble()

            val dLat = Math.toRadians(eventLatitude - user.lattitude as Double)
            val dLon = Math.toRadians(eventLongitude - user.longitude as Double)

            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(user.lattitude as Double)) * Math.cos(Math.toRadians(eventLatitude)) *
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

