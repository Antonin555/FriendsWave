package com.antonin.friendswave.data.model

import com.antonin.friendswave.adapter.ListItemViewModel

data class Messages (
    var message: String? = "",
    var senderId: String? = "",
    var senderName: String? = ""
        ): ListItemViewModel()


