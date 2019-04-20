package com.example.utente10.galileo.bean

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class DBVersion : RealmModel {
    var version: Int? = null
}