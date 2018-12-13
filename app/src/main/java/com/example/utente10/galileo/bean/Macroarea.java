package com.example.utente10.galileo.bean;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class Macroarea implements RealmModel {
    String name;
    LatLng center;
    float radius;
    RealmList<Landmark> landmarks;
}
