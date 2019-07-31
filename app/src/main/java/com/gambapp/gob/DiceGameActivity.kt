package com.gambapp.gob

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.manager.DiceManager
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.services.AnalyticsService
import kotlinx.android.synthetic.main.activity_dice_game.*

class DiceGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dice_game)

        grosPoulet.visibility = View.GONE

        val game_id = intent.getSerializableExtra("game_id") as Int
        val game = RealmDbHelper().get<RealmGame>(game_id.toLong())
        val manager = DiceManager(game!!)


        exitDiceButton.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Voulez vous vraiment quitter la partie ?")

            builder.setPositiveButton("Quitter"){_, _ ->
                val intent = Intent(this, GameDetailActivity::class.java)
                intent.putExtra("game_id",game_id)
                AnalyticsService.instance.eventClose()
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Annuler"){_,_ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
        diceGameLayout.setOnClickListener {
            val anim1 = AnimationUtils.loadAnimation(this@DiceGameActivity, R.anim.shake)
            val animationListener = object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    manager.letsGetRandom(this@DiceGameActivity)
                    if(manager.sum == 3) grosPoulet.visibility = View.VISIBLE else{
                        grosPoulet.visibility = View.GONE
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            }

            anim1.setAnimationListener(animationListener)

            simple_anim.startAnimation(anim1)
            simple_anime.startAnimation(anim1)
        }
    }


}
