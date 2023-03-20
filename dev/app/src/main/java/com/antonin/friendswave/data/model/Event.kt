package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel

data class Event(

    var name: String? = "",
    var isPublic : Boolean? = true,
    var nbrePersonnes : Int? = 0,
    var admin:String = "",
    var categorie : String? = "",
    var description : String? = "",
    var date: String? = "",
    var heure: String? = "",
    var lattitude : String = "",
    var longitude : String = "",
    var duree: Int? = 0,
    var listInscrits : HashMap<String, String> = HashMap(),
    var listAttente : HashMap<String, String> = HashMap(),

):ListItemViewModel()