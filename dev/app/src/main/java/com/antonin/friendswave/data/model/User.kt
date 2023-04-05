package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel



data class User (
    var name: String? = "",
    var email: String? = "",
    var uid: String? = "",
    var familyName : String? = "",
    var desciption: String? = "",
    var age: Int = 0,
    var lieu: String? = "",
    var langue: String? = "Francais",
    var etude: String? = "Self-Made",
    var rating: Double? =0.0,
    var friends : Int? = 1,
    var interet : ArrayList<String>? = ArrayList(),
    var friendRequest: HashMap<String, String>? = HashMap(),
    var friendList: HashMap<String, String>? = HashMap(),
    var invitations: HashMap<String,String>? = HashMap(),
    var eventConfirmationList: HashMap<String,String>? = HashMap(),
    var nbre_event : Int? =0,
    var img:String?  = ""

    ): ListItemViewModel()
