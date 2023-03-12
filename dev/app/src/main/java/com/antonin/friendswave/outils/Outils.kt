package com.antonin.friendswave.outils

import android.content.Context
import android.content.Intent
import com.antonin.friendswave.ui.authentification.LoginActivity
import com.antonin.friendswave.ui.event.AddEventActivity
import com.antonin.friendswave.ui.home.EditProfilActivity
import com.antonin.friendswave.ui.home.HomeActivity

fun Context.startHomeActivity() =
    Intent(this, HomeActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.startLoginActivity() =
    Intent(this, LoginActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
//
//
//fun Context.startContactActivity() =
//    Intent(this, ContactActivity::class.java).also {
//        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(it)
//    }
//
fun Context.startEditProfilActivity() =
    Intent(this, EditProfilActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }