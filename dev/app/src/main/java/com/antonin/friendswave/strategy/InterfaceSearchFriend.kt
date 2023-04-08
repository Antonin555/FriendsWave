package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.User

interface InterfaceSearchFriend {

    fun sortedUser(user: User?, totalUser: List<User>?): List<User>
}