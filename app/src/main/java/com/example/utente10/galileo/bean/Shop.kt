package com.example.utente10.galileo.bean

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
data class Shop(
    val name: String,
    val description: String,
    val beacon: Beacon,
    val photos: RealmList<String>? = null) : RealmModel