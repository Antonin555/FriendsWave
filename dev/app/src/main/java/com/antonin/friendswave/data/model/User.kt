package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel

//class User (name: String?, email: String?, uid: String?, friendList: ArrayList<User>? = null, pendingFriendRequest: ArrayList<User>? = null, eventList: ArrayList<Event>? = null, pendingEventList: ArrayList<Event>? = null, photo: Int? = null, description: String? = null, age: Int? = null, lieu: String? = null, langueParle: ArrayList<String>? = null, etude: String? = null, interet: ArrayList<String>? = null) : ListItemViewModel(){
//
//    var name: String? = name
//    var email: String? = email
//    var uid: String? = uid
//    var friendList: ArrayList<User>? = friendList
//    var pendingFriendRequest: ArrayList<User>? = pendingFriendRequest
//    var eventList: ArrayList<Event>? = eventList
//    var pendingEventList: ArrayList<Event>? = pendingEventList
//    var photo: Int? = photo
//    var description: String? = description
//    var age: Int? = age
//    var lieu: String? = lieu
//    var langueParle: ArrayList<String>? = langueParle
//    var etude: String? = etude
//    var interet: ArrayList<String>? = interet
//
//
//
//}


data class User (

    var name: String? = "",
    var email: String? = "",
    var uid: String? = "",
    var rating: Double? =0.0,
    var friends : Int? = 1



    ): ListItemViewModel()