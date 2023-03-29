package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel

data class Event(

    var key : String? = "",
    var name: String? = "",
    var isPublic : Boolean? = true,
    var nbrePersonnes : Int? = 0,
    var admin:String = "",
    var categorie : String? = "",
    var date: String? = "",
    var heure: String? = "",
    var adress:String? = "",
    var description : String? = "",
    var lattitude : String = "",
    var longitude : String = "",
    var duree: Int? = 0,
    var listInscrits : HashMap<String, String> = HashMap(),
    var invitations : HashMap<String, String> = HashMap(),

):ListItemViewModel()