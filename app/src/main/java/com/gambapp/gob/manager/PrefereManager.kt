package com.gambapp.gob.manager


import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.model.Realm.RealmPlayer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PrefereManager {


    private var sum = 0
    private var positionPlayer: Int = 0
    private val players = RealmDbHelper().getAll<RealmPlayer>()
    private val dictionnary = HashMap<String, Int>()
    private val maxRaise = 4
    var currentRaise = 1
    private var listOfScore: ArrayList<Map.Entry<String, Int>> = arrayListOf()

    init {
        setupDictionnary()
    }

    private fun setupDictionnary() {
        for (p in players) {
            dictionnary[p.player_name] = 0
        }
        val entrySet = dictionnary.entries

        val listOfEntry = ArrayList<Map.Entry<String, Int>>(entrySet)
        listOfScore = listOfEntry
        listOfScore.maxBy { it.value }
    }




    fun setupPlayer(code: Int,position:Int) {

        val player = this.players[position]
        if (code == 1) {
            dictionnary[player!!.player_name] = dictionnary[player.player_name]!! + sum

        } else {
            dictionnary[player!!.player_name] = dictionnary[player.player_name]!!

        }




    }

    fun recommencer() {
        currentRaise = 1
        positionPlayer = 0
        listOfScore.clear()
        setupDictionnary()

    }

    private fun randomValue() {
        sum = 1
    }

    fun getDictionnary(): ArrayList<Map.Entry<String, Int>> = listOfScore


    fun letsGetRandom(playerName: Int) {
        this.randomValue()
        this.setupPlayer(1,playerName)
    }

}
