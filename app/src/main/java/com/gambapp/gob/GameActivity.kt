package com.gambapp.gob

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gambapp.gob.helpers.ColorHelper
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.manager.QuestionManager
import com.gambapp.gob.model.Question
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.services.AnalyticsService
import com.gambapp.gob.services.ReportService
import kotlinx.android.synthetic.main.activity_game.*




class GameActivity : AppCompatActivity() {

    private var currentQuestion: Question? = null
    private var currentColor: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val game_id = intent.getSerializableExtra("game_id") as Int
        val game = RealmDbHelper().get<RealmGame>(game_id.toLong())
        val gameManager = QuestionManager(game!!)
        setupQuestions(gameManager)

        gameLayout.setOnClickListener {
            setupQuestions(gameManager)
            errorButton.visibility = View.VISIBLE

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
            var newColor = ColorHelper().getColorByType(question.type)
            if (currentColor == null){
                currentColor = newColor
            }
            gameLayout.setBackgroundColor(currentColor!!)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), (resources.getColor(currentColor!!)), resources.getColor(newColor!!))
            colorAnimation.duration = 500 // milliseconds
            colorAnimation.addUpdateListener { animator -> gameLayout.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()
            currentColor = ColorHelper().getColorByType(question.type)

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
