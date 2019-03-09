package com.example.utente10.galileo.bean

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class Coordinates : RealmModel {
    var latitude: Double? = null
    var longitude: Double? = null

    override fun equals(other: Any?): Boolean {
        if (other !is Coordinates)
            return false
        if (other.latitude != this.latitude)
            return false
        if (other.longitude != this.longitude)
            return false
        return true
    }
}