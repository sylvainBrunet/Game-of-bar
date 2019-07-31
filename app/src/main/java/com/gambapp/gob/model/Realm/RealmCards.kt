package com.gambapp.gob.model.Realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmCards : RealmObject() {

    var game_id :Int = 0
    @PrimaryKey
    var id :Int = 0
    var number :String? = null
    var symbol :String? = null
    var content :String? = null
    var drink :Int = 0

}

