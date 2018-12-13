package com.example.utente10.galileo.bean

import io.realm.RealmList
import io.realm.RealmModel
import com.google.android.gms.maps.model.LatLng
import io.realm.annotations.RealmClass

@RealmClass
data class Macroarea(val name: String,
                     val center: LatLng,
                     val radius: Float,
                     val landmarks: RealmList<Landmark>) : RealmModel