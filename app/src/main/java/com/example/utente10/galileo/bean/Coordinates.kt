package com.example.utente10.galileo.bean

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class Coordinates : RealmModel {
    var latitude: Double? = null
    var longitude: Double? = null
}