package com.gambapp.gob.model

import com.gambapp.gob.model.Realm.RealmCards

class Card(questionObject: RealmCards) {
    var game_id :Int = questionObject.game_id
    var id: Int = questionObject.id
    var number:String? = questionObject.number
    var symbol: String? = questionObject.symbol
    var content:String = questionObject.content!!
    var drink:Int = questionObject.drink


}
