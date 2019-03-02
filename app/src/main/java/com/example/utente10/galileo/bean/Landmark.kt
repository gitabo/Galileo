package com.example.utente10.galileo.bean

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class Landmark : RealmModel {
    var name: String? = null
    var beacon: Beacon? = null
    var description: String? = null
    var content_url: String? = null
    var img_name: String? = null
}
