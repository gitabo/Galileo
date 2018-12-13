package com.example.utente10.galileo.bean

import com.google.android.gms.maps.model.LatLng
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Beacon : RealmModel {
    @PrimaryKey
    var label: String? = null
    var coordinates: Coordinates? = null
}