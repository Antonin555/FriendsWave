package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel

data class Friends (

    var name:String? = "",
    var email : String? = ""
): ListItemViewModel()
