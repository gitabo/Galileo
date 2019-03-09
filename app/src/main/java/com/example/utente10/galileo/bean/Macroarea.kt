package com.example.utente10.galileo.bean

import io.realm.RealmList
import com.google.android.gms.maps.model.LatLng
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class Macroarea : RealmModel {
    var name: String? = null
    var center: Coordinates? = null
    var radius: Double? = null  //in meter!!
    var description: String? = null
    var landmarks: RealmList<Landmark>? = null

    override fun equals(other: Any?): Boolean {
        if (other !is Macroarea)
            return false
        if (other.name != this.name)
            return false
        if (other.center != this.center)
            return false
        return true
    }
}