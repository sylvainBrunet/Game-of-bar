package com.gambapp.gob.model.Realm

import io.realm.RealmObject

open class RealmOwnedGame : RealmObject() {

        var game: RealmGame? = RealmGame()
        var exipiration_date: String = ""
        var acquisition_date: String? = ""
        var acquisition_code: String? = null
        var position: Int? = null

}
