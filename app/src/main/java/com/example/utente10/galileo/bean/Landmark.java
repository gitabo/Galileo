package com.example.utente10.galileo.bean;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class Landmark  implements RealmModel{
    String name;
    Beacon beacon;
    RealmList<String> videos;
    RealmList<String> photos;
    String description;
}
