package com.gambapp.gob.services

import com.gambapp.gob.model.Realm.RealmGame
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashMap

class AnalyticsService {
    companion object {
        val instance = AnalyticsService()
    }

    private lateinit var currentGame:RealmGame
    private lateinit var uuid:UUID

    private val rootRef = FirebaseDatabase.getInstance().reference.child("stats").child("games")
    private val platform:String = "ANDROID"

    private val data:HashMap<String, Any> = HashMap()
    private var sips:Int = 0
    private var players:Int = 0

    fun eventStart() {
        data["started_at"] = Date().time
        saveData()
    }

    fun eventFinish() {
        data["finished_at"] = Date().time
        saveData()
    }

    fun eventClose() {
        data["closed_at"] = Date().time
        saveData()
    }

    val tag = "analytics"
    fun saveData() {
        if (data.size > 0) {
            rootRef.child(uuid.toString()).setValue(data)
        }
    }

    fun setDate(game:RealmGame, players:Int, sips:Int){
        data.clear()

        this.currentGame = game
        this.players = players
        this.sips = sips
        this.uuid = UUID.randomUUID()

        data["id"] = currentGame.id
        data["plateform"] = platform
        data["players"] = players
        data["sips"] = sips
    }

    fun getPlayers(): Int {
        return this.players
    }
}
