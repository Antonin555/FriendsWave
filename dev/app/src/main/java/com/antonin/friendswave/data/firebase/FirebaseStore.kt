package com.antonin.friendswave.data.firebase

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

//Documentation https://firebase.google.com/docs/storage/android/create-reference?hl=fr

class FirebaseStore {


    var storage: FirebaseStorage = Firebase.storage

    fun displayImage(imgView:ImageView, path: String){

        val storageRef = storage.reference.child(path)

        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(imgView.context)
                .load(it)
                .apply(RequestOptions().override(100, 100))
                .centerCrop()
                .into(imgView)
        }.addOnFailureListener {
            println(it)
        }

    }


}