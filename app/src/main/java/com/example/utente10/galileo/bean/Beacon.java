package com.example.utente10.galileo.bean;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
class Beacon implements RealmModel{
    @PrimaryKey
    String label;
    LatLng coordinates;
}
