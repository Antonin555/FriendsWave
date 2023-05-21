package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.User

class StrategyFriend(private var interfaceSearchFriend : InterfaceSearchFriend) {

    fun search(user: User?, list: List<User>?) : List<User> {
        return interfaceSearchFriend.sortedUser(user, list)
    }
}