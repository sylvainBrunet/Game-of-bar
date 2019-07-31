package com.gambapp.gob.model.Realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RealmGame : RealmObject() {
  @PrimaryKey var id :Int = 0
  var type :Int = 0
  var title :String = ""
  var description :String = ""
  var price :Double = 0.0
  var version :String = ""

  var questions_number = RealmList<RealmQuestionNumber>()
  var preview = RealmList<RealmPreview>()
}

open class RealmQuestionNumber : RealmObject(){
  var type :Int = 0
  var number :Int = 0
}

open class RealmPreview : RealmObject(){
  var preview :String? = null
}
