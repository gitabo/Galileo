package com.example.utente10.galileo.backend

import com.example.utente10.galileo.bean.Macroarea
import io.realm.RealmList

data class MacroareasResponse(val macroareas: RealmList<Macroarea>)