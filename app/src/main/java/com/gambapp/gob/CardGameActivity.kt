package com.gambapp.gob

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.manager.CardManager
import com.gambapp.gob.model.Card
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.services.AnalyticsService
import com.gambapp.gob.services.ReportService
import kotlinx.android.synthetic.main.activity_card_game.*

class CardGameActivity : AppCompatActivity() {

  private var currentCard: Card? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_card_game)


    val game_id = intent.getSerializableExtra("game_id") as Int
    val game = RealmDbHelper().get<RealmGame>(game_id.toLong())
    game?.let { _ ->
      val gameManager = CardManager(game)
      setupCard(gameManager)
      cardGameLayout.setOnClickListener {
        setupCard(gameManager)
        errorCardButton.visibility = View.VISIBLE
      }
    }

    exitCardButton.setOnClickListener{
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

    errorCardButton.setOnClickListener {_ ->
      val builder = AlertDialog.Builder(this)
      builder.setMessage("Signaler cette carte")

      builder.setPositiveButton("Envoyer"){_, _ ->
        currentCard.let {
          ReportService().reportCard(it!!)
          errorCardButton.visibility = View.GONE
        }
      }

      builder.setNegativeButton("Annuler"){_,_ ->

      }

      val dialog: AlertDialog = builder.create()
      dialog.show()

      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
      dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }
  }

  private fun setupCard(manager: CardManager) {
    if (manager.getFinalCard().isEmpty()) {
      val gameId = manager.game.id
      val intent = Intent(this, EndGameActivity::class.java)
      intent.putExtra("game_id",gameId)
      startActivity(intent)
    } else {
      val card = manager.getFinalCard().first()
      currentCard = card

      card.let {

        val translate: Animation = AnimationUtils.loadAnimation(this, R.anim.translateyaxes)
        val animationListener = object : Animation.AnimationListener {
          override fun onAnimationStart(animation: Animation) {
            cardSymbol.visibility = View.GONE
          }

          override fun onAnimationEnd(animation: Animation) {
              cardSymbol.visibility = View.VISIBLE

              contentCard.text = card.content
              cardNumber.text = card.number
              val img = resources.getIdentifier("${card.symbol}", "drawable", packageName)
              cardSymbol.setImageBitmap(BitmapFactory.decodeResource(resources, img))
              cardSymbolMain.setImageBitmap(BitmapFactory.decodeResource(resources, img))
              if (card.symbol == "diamonds" || card.symbol == "heards") {
                  cardNumber.setTextColor(ContextCompat.getColor(applicationContext, R.color.redCard))
              } else {
                  cardNumber.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
              }
              manager.removeFirstQuestionInFinalCard()
          }

          override fun onAnimationRepeat(animation: Animation) {
          }
        }

          translate.setAnimationListener(animationListener)
          playersViewDetails.startAnimation(translate)
          cardSymbol.startAnimation(translate)
          cardSymbolMain.startAnimation(translate)
          cardNumber.startAnimation(translate)

      }
    }
  }

}
