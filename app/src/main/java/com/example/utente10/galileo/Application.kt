package com.example.utente10.galileo

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
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