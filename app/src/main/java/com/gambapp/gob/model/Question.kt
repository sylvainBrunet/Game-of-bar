package com.gambapp.gob.model

import com.gambapp.gob.model.Realm.RealmQuestion

class Question(questionObject: RealmQuestion) {
    var id: Int = questionObject.id
    var key: String? = questionObject.key
    var parent_key:String? = questionObject.parent_key
    var title:String? = questionObject.title
    var content:String = questionObject.content!!
    var drink:Int = questionObject.drink!!
    var type: Int = questionObject.type

}

