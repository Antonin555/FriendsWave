package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel

data class Message (
    var message: String? = "",
    var senderId: String? = ""
        ): ListItemViewModel()


