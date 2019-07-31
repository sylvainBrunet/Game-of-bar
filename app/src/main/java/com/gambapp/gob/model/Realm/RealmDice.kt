package com.gambapp.gob.model.Realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmDice : RealmObject() {

  var game_id :Int = 0
  @PrimaryKey
  var id :Int = 0
  var type :String? = null
  var number :Int = 0
  var content :String? = null
  var drink :Int = 0
}
