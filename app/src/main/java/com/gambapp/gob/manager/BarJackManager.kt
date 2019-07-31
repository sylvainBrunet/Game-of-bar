package com.gambapp.gob.manager

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.gambapp.gob.BarJackActivity
import com.gambapp.gob.R
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.model.Realm.RealmPlayer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BarJackManager(cont: Context) {

    private val random = Random()
    private var valueDice1 = 0
    private var valueDice2 = 0
    private var sum = 0
    private var positionPlayer: Int = 0
    private var positionNextPlayer: Int = 1
    private val players = RealmDbHelper().getAll<RealmPlayer>()
    private val maniActivity = cont as BarJackActivity
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


    private fun setupDice(cont: Context) {
        val dice1 = maniActivity.findViewById<View>(R.id.first_dice) as ImageView
        val dice2 = maniActivity.findViewById<View>(R.id.second_dice) as ImageView

        val res1 = cont.resources.getIdentifier("dice_$valueDice1", "drawable", cont.packageName)
        val res2 = cont.resources.getIdentifier("dice_$valueDice2", "drawable", cont.packageName)

        dice1.setImageResource(res1)
        dice2.setImageResource(res2)
    }

    fun setupPlayer(code: Int) {

        val player = this.players[positionPlayer]
        val nextPlayerRealm = this.players[positionNextPlayer]
        if (code == 1) {
            dictionnary[player!!.player_name] = dictionnary[player.player_name]!! + sum

        } else {
            dictionnary[player!!.player_name] = dictionnary[player.player_name]!!

        }
        val currentPlayer = maniActivity.findViewById<View>(R.id.currentPlayer) as TextView
        val nextPlayer = maniActivity.findViewById<View>(R.id.nextPlayer) as TextView
        val raise = maniActivity.findViewById<View>(R.id.roundNb) as TextView
        raise.text = "Round $currentRaise"

        val recommencer = maniActivity.findViewById<View>(R.id.recommencer) as Button
        val lancerlesdes = maniActivity.findViewById<View>(R.id.lancerLesDes) as Button
        lancerlesdes.visibility = View.VISIBLE


        currentPlayer.text = player!!.player_name + " viens de faire $sum"
        nextPlayer.text = "Prochain joueur : "+ nextPlayerRealm!!.player_name

        positionPlayer += 1
        positionNextPlayer += 1

        if (positionPlayer > players.count() - 1) {
            currentRaise += 1
            positionPlayer = 0
            if (currentRaise == maxRaise) {
                recommencer.visibility = View.VISIBLE
                lancerlesdes.visibility = View.GONE
            }
        }
        if (positionNextPlayer > players.count() - 1) {
            positionNextPlayer = 0
        }
    }

    fun recommencer() {
        currentRaise = 1
        positionPlayer = 0
        listOfScore.clear()
        setupDictionnary()

    }

    private fun randomValue() {
        valueDice1 = randomDiceValue()
        valueDice2 = randomDiceValue()
        sum = valueDice1 + valueDice2
    }

    private fun randomDiceValue(): Int = random.nextInt(6) + 1
    fun getDictionnary(): ArrayList<Map.Entry<String, Int>> = listOfScore


    fun letsGetRandom(cont: Context) {
        this.randomValue()
        this.setupPlayer(1)
        this.setupDice(cont)
    }

}
