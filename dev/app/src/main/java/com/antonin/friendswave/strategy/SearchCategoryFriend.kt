package com.antonin.friendswave.strategy

import com.antonin.friendswave.data.model.User

class SearchHobbyFriend: InterfaceSearchFriend {

    override fun sortedUser(mainUser: User?, totalUser: List<User>?): List<User> {

        var similarUsers : ArrayList<User> = ArrayList()

        for (user in totalUser!!){
            if (user != mainUser){
                //p-e verifier si il y a des interets dans la liste des utilisateurs
                val interests1 = mainUser?.interet!!.toSet()
                val interests2 = user.interet!!.toSet()

                val intersection = interests1.intersect(interests2)
                val union = interests1.union(interests2)
                val similarity = intersection.size.toDouble() / union.size.toDouble()
                //plus grand que 0.5 car l'algo renvoie un chiffre entre 0 et 1 ou 1 signifie une similarite complete
                if(similarity >= 0.5){
                    similarUsers.add(user)
                }
            }
        }
        return similarUsers
    }

}

class SearchCityFriend: InterfaceSearchFriend {


    override fun sortedUser(mainUser: User?, totalUser: List<User>?): List<User> {
        var similarUsers : ArrayList<User> = ArrayList()

        for(user in totalUser!!){
            if(user != mainUser){
                if (user.lieu == mainUser!!.lieu){
                    similarUsers.add(user)
                }
            }
        }

        return similarUsers
    }

}

class SearchAgeFriend: InterfaceSearchFriend {


    override fun sortedUser(mainUser: User?, totalUser: List<User>?): List<User> {
        var similarUsers : ArrayList<User> = ArrayList()

        //lambda pour aller chercher les utilisateur qui ont plus ou moin 5 ans
        similarUsers = totalUser!!.filter { user ->
            user.age in (mainUser!!.age - 5)..(mainUser.age + 5)
        } as ArrayList<User>

        return similarUsers

    }

}