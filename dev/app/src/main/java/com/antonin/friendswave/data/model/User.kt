package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel


data class User (
    var name: String? = "",
    var email: String? = "",
    var uid: String? = "",
    var rating: Double? =0.0,
    var friends : Int? = 1,
    var friendRequest: HashMap<String, String>? = HashMap(),
    var friendList: HashMap<String, String>? = HashMap(),
    var invitations: HashMap<String,String>? = HashMap(),
    var eventConfirmationList: HashMap<String,String>? = HashMap(),
    var nbre_event : Int? =0

    ): ListItemViewModel()