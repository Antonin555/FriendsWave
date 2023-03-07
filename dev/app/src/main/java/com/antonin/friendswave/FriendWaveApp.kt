package com.antonin.friendswave

import android.app.Application

import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityLoginBinding.bind
import com.antonin.friendswave.ui.authentification.AuthViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton



class FriendWaveApp : Application(), KodeinAware {


    override val kodein = Kodein.lazy {
        import(androidXModule(this@FriendWaveApp))

        bind() from singleton { FirebaseSource() }
        bind() from singleton { UserRepo(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }

    }
}
