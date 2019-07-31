package com.gambapp.gob

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gambapp.gob.adapter.BarJackPlayerAdapter
import com.gambapp.gob.manager.BarJackManager
import kotlinx.android.synthetic.main.activity_bar_jack.*

class BarJackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_jack)
        val manager = BarJackManager(this)

        playerScore.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        playerScore.adapter = BarJackPlayerAdapter(manager.getDictionnary())
        recommencer.visibility = View.GONE

        recommencer.setOnClickListener {
            manager.recommencer()
            playerScore.adapter = BarJackPlayerAdapter(manager.getDictionnary())
            playerScore.adapter.notifyDataSetChanged()
            recommencer.visibility = View.GONE

        }
        passButton.setOnClickListener {
            if (manager.currentRaise == 4) {
                recommencer.visibility = View.VISIBLE
            } else {
                manager.setupPlayer(0)
            }
        }

        lancerLesDes.setOnClickListener {
            if (manager.currentRaise == 4) {
                recommencer.visibility = View.VISIBLE
            } else {

                val anim1 = AnimationUtils.loadAnimation(this@BarJackActivity, R.anim.shake)
                val animationListener = object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        if (manager.currentRaise == 4) {
                            recommencer.visibility = View.VISIBLE
                        } else {
                            manager.letsGetRandom(this@BarJackActivity)
                            playerScore.adapter.notifyDataSetChanged()
                        }
                    }
                    override fun onAnimationRepeat(animation: Animation) {}
                }
                anim1.setAnimationListener(animationListener)

                first_dice.startAnimation(anim1)
                second_dice.startAnimation(anim1)
            }

        }
    }
}
