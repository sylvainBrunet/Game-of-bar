package com.gambapp.gob.model.Realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmQuestion : RealmObject() {
    @PrimaryKey var id :Int = 0
    var key :String? = null
    var parent_key :String? = null
    var title :String? = null
    var content :String? = null
    var type :Int = 0
    var drink :Int? = null
}
