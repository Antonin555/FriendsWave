package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel

data class Event(

    var name: String? = "",
    var isPublic : Boolean? = true,
    var nbrePersonnes : Int? = 0,
//    var categorie : String? = "",
//    var description : String? = "",
//    var date: String? = "",
//    var lattitude : String = "",
//    var longitude : String = ""

):ListItemViewModel()