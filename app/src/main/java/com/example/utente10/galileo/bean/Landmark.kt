package com.example.utente10.galileo.bean

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
data class Landmark(val name: String,
                    val beacon: Beacon,
                    var description: String,
                    val videos: RealmList<String>? = null,
                    val photos: RealmList<String>? = null) : RealmModel
