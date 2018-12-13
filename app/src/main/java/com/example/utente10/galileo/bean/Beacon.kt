package com.example.utente10.galileo.bean

import com.google.android.gms.maps.model.LatLng
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
data class Beacon (@PrimaryKey val label: String,
                   val coordinates: LatLng) : RealmModel