package com.antonin.friendswave.data.firebase

import android.widget.ImageView
import com.antonin.friendswave.R
import com.antonin.friendswave.data.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FirebaseStorage {


    var storage: FirebaseStorage = Firebase.storage

    fun displayProfil(imgView:ImageView, user: User, path: String){

        val storageRef = storage.reference.child(path + user.img.toString())

        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(imgView.context)
                .load(it)
                .placeholder(R.drawable.user)
                .apply(RequestOptions().override(100, 100))
                .centerCrop()
                .into(imgView)
        }.addOnFailureListener {
            println(it)
        }

    }

}