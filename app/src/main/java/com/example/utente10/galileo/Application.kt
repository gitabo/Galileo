package com.example.utente10.galileo

import android.content.Intent
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.utente10.galileo.service.TrackerService
import io.realm.Realm
import io.realm.RealmConfiguration

class Application : android.app.Application() {

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder().name("Galileo.realm").build()
        Realm.setDefaultConfiguration(config)
    }
}