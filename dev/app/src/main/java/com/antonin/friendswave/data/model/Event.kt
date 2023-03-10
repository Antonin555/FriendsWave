package com.antonin.friendswave.data.model

data class Event(

    var name: String? = "",
    var isPublic : Boolean? = true,
    var nbrePersonnes : Int? = 0,
    var categorie : String? = "",
    var description : String? = "",
    var date: String? = "",
    var isActive: Boolean = false,
    var lattitude : String = "",
    var longitude : String = ""

)