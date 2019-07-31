package com.gambapp.gob


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.manager.QuestionManager
import com.gambapp.gob.model.Question
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.services.AnalyticsService
import com.gambapp.gob.services.ReportService
import com.gambapp.gob.model.Realm.RealmPlayer
import android.util.Log
import android.widget.Button
import com.gambapp.gob.adapter.ButtonPlayerList
import com.gambapp.gob.adapter.PrefereScoreAdapter
import com.gambapp.gob.manager.PrefereManager
import kotlinx.android.synthetic.main.activity_prefere.*


class PrefereActivity : AppCompatActivity() {

    private var currentQuestion: Question? = null
    private val round = 4
    private var currentRound = 0
    private val maxMatch = 4
    private var currentMatch = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefere)
        val playerName = RealmDbHelper().getAll<RealmPlayer>()


        val manager = PrefereManager()

        val adapter = ButtonPlayerList(playerName,this,manager)
        buttonList.adapter = adapter
        buttonList.layoutManager = GridLayoutManager (this, 2)


        val game_id = intent.getSerializableExtra("game_id") as Int
        val game = RealmDbHelper().get<RealmGame>(game_id.toLong())
        val gameManager = QuestionManager(game!!)
        setupQuestions(gameManager)


        relaunchButton.setOnClickListener {
            buttonList.adapter = ButtonPlayerList(playerName,this,manager)
            buttonList.layoutManager = GridLayoutManager (this, 2)
            if (currentRound >= round) {
                if (currentRound == maxMatch) {
                    currentMatch += 1
                }

                val view = layoutInflater.inflate(R.layout.prefere_end_game_layout, null)
                val rv = view.findViewById(R.id.rv_player_score) as RecyclerView
                val tv = view.findViewById(R.id.buyButton) as Button
                tv.text = "CONTINUER"
                rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rv.adapter = PrefereScoreAdapter(manager.getDictionnary())
                val dialog = BottomSheetDialog(this)
                if (currentMatch == 4) {
                    tv.text = "TERMINER"
                }
                tv.setOnClickListener {
                    manager.recommencer()
                    dialog.hide()
                    currentRound = 0
                    if (currentMatch == maxMatch) {

                        val intent = Intent(this, EndGameActivity::class.java)
                        intent.putExtra("game_id", game_id)
                        startActivity(intent)
                    }
                    setupQuestions(gameManager)
                }
                dialog.setContentView(view)
                dialog.show()

            } else {


                setupQuestions(gameManager)
                currentRound += 1
                Log.d("tag",manager.getDictionnary().toString())
                errorButton.visibility = View.VISIBLE

            }
        }
        exitButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Voulez vous vraiment quitter la partie ?")
            builder.setPositiveButton("Quitter") { _, _ ->
                val intent = Intent(this, GameDetailActivity::class.java)
                intent.putExtra("game_id", game_id)
                AnalyticsService.instance.eventClose()
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Annuler") { _, _ ->

            }

            val dialog: AlertDialog = builder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }

        errorButton.setOnClickListener { _ ->
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Signaler cette question")

            builder.setPositiveButton("Envoyer") { _, _ ->
                currentQuestion.let {
                    ReportService().reportQuestion(it!!)
                    errorButton.visibility = View.GONE
                }
            }

            builder.setNegativeButton("Annuler") { _, _ ->

            }

            val dialog: AlertDialog = builder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
    }

    private fun setupQuestions(manager: QuestionManager) {

        if (manager.getFinalQuestions().isEmpty()) {
            val gameId = manager.game.id
            val intent = Intent(this, EndGameActivity::class.java)
            intent.putExtra("game_id", gameId)
            startActivity(intent)
        } else {
            val question = manager.getFinalQuestions().first()
            currentQuestion = question


            val translateOut: Animation = AnimationUtils.loadAnimation(this, R.anim.translateout)
            val animationListenerOut = object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    content_questions.visibility = View.GONE
                }

                override fun onAnimationEnd(animation: Animation) {
                    content_questions.visibility = View.VISIBLE
                    val translateIn: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.translate)
                    val animationListenerIn = object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {
                            content_questions.text = question.content

                        }

                        override fun onAnimationEnd(animation: Animation) {

                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    }
                    translateIn.setAnimationListener(animationListenerIn)
                    content_questions.startAnimation(translateIn)
                    if (question.title == null) {
                        title_questions.visibility = View.GONE
                    } else {
                        val animationFadeIn: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
                        val animationListener = object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}
                            override fun onAnimationEnd(animation: Animation) {}
                            override fun onAnimationRepeat(animation: Animation) {}
                        }
                        animationFadeIn.setAnimationListener(animationListener)
                        title_questions.startAnimation(animationFadeIn)
                        title_questions.visibility = View.VISIBLE
                        title_questions.text = question.title
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {
                }
            }
            translateOut.setAnimationListener(animationListenerOut)
            content_questions.startAnimation(translateOut)
            manager.removeFirstQuestionInFinalQuestions()

        }
    }
}

