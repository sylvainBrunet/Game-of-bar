package com.gambapp.gob.model.Realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmPlayer : RealmObject() {

  @PrimaryKey
  var player_name :String = ""
}
