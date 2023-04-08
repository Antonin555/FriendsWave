package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.User

class StrategyFriend(private var interfaceSearchFriend : InterfaceSearchFriend) {

//    fun searchByHobby(user: User, list: List<User>?) : List<User> {
//        return interfaceSearchFriend.sortedUser(user, list)
//    }
//
//    fun searchByCity(user: User, list: List<User>?) : List<User> {
//        return interfaceSearchFriend.sortedUser(user, list)
//    }

    fun search(user: User?, list: List<User>?) : List<User> {

        return interfaceSearchFriend.sortedUser(user, list)
    }
}