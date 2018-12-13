package com.example.utente10.galileo.bean;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class Shop implements RealmModel {
    String name;
    String description;
    Beacon beacon;
    RealmList<String> photos;
}
