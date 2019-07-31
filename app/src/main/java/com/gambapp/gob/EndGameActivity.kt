package com.gambapp.gob

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.services.AnalyticsService
import kotlinx.android.synthetic.main.activity_end_game.*


class EndGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsService.instance.eventFinish()
        setContentView(R.layout.activity_end_game)
        val game_id = intent.getIntExtra("game_id", 1)
        val game = RealmDbHelper().get<RealmGame>(game_id.toLong())
        setupGameDetail(game!!)

        recommence.setOnClickListener {
            val intentQuestion = Intent(this, GameActivity::class.java)
            intentQuestion.putExtra("game_id", game_id)
            val intent = Intent(this, CardGameActivity::class.java)
            intent.putExtra("game_id", game_id)
            val intentDice = Intent(this, DiceGameActivity::class.java)
            intentDice.putExtra("game_id", game_id)
            val intentBarJack = Intent(this, BarJackActivity::class.java)
            intentBarJack.putExtra("game_id", game_id)

            val intentTuPrefere = Intent(this, PrefereActivity::class.java)
            intentTuPrefere.putExtra("game_id", game_id)

            val analyticsManager = AnalyticsService.instance
            analyticsManager.setDate(game, analyticsManager.getPlayers(), 0)
            analyticsManager.eventStart()

            when (game_id) {
                1 -> startActivity(intentQuestion)
                2 -> startActivity(intent)
                3 -> startActivity(intent)
                4 -> startActivity(intentDice)
                5 -> startActivity(intent)
                6 -> startActivity(intentQuestion)
                7 -> startActivity(intentQuestion)
                8 -> startActivity(intentBarJack)
                9 -> startActivity(intentTuPrefere)
                else -> { // Note the block

                }
            }
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun setupGameDetail(game: RealmGame) {
        endGameName.text = "Tu as aimé le ${game.title} ?"
    }
}
