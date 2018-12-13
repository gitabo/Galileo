package com.example.utente10.galileo.bean

import io.realm.RealmList
import io.realm.RealmModel

open class Shop : RealmModel{
    var name: String? = null
    var description: String? = null
    var beacon: Beacon? = null
    var photos: RealmList<String>? = null
}