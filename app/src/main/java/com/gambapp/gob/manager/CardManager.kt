package com.gambapp.gob.manager

import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.helpers.TextHelpers
import com.gambapp.gob.model.Card
import com.gambapp.gob.model.Realm.RealmCards
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.model.Realm.RealmPlayer
import io.realm.RealmResults
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class CardManager(var game: RealmGame) {

    private val players = RealmDbHelper().getAll<RealmPlayer>()
    private val finalCard: CopyOnWriteArrayList<Card> = CopyOnWriteArrayList()
    var positionPlayer: Int = 0
    private val random = Random()


    init {
        this.getCards()
        finalCard.shuffle()
        this.setupPlayer()
        finalCard.shuffle()
    }


    private fun getCards() {
        val tmpCards: RealmResults<RealmCards> = RealmDbHelper().filterOne("game_id", game.id)
        for (card in tmpCards) {
            val finalCard = Card(card)
            this.finalCard.add(finalCard)
        }
    }
    private fun setupPlayer() {

        for (e in 0 until this.finalCard.count()) {
            val player = this.players[positionPlayer]
            val playerPosition2 = (0 until this.players.count()).random()
            var player2 = this.players[playerPosition2]

            if (this.players.count() > 1) {
                while (player!! == player2) {
                    val playerPosition2 = (0 until this.players.count()).random()
                    player2 = this.players[playerPosition2]
                }
            }

            var dummy = this.finalCard[e].content
            val capitalisePlayer = TextHelpers().setFirstCapital(player!!.player_name)
            val capitalisePlayer2 = TextHelpers().setFirstCapital(player2!!.player_name)

            dummy = dummy
                    .replace("%d", this.finalCard[e]!!.drink.toString(), false)
                    .replace("%p", capitalisePlayer, false)
                    .replace("%o",capitalisePlayer2, false)
            this.finalCard[e].content = dummy
            if (this.finalCard[e].symbol != "J") {
                positionPlayer += 1
            }
            if (positionPlayer > players.count() - 1) {
                positionPlayer = 0
            }
        }
    }

    fun getFinalCard(): CopyOnWriteArrayList<Card> {
        return this.finalCard
    }

    fun removeFirstQuestionInFinalCard() {
        this.finalCard.removeAt(0)
    }
    fun IntRange.random() = (Math.random() * ((endInclusive + 1) - start) + start).toInt()

}