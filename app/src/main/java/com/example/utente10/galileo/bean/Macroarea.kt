package com.example.utente10.galileo.bean

import io.realm.RealmList
import com.google.android.gms.maps.model.LatLng
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class Macroarea : RealmModel {
    var name: String? = null
    var center: Coordinates? = null
    var radius: Double? = null
    var landmarks: RealmList<Landmark>? = null
}