package com.gambapp.gob.manager

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gambapp.gob.DiceGameActivity
import com.gambapp.gob.R
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.model.Realm.RealmDice
import com.gambapp.gob.model.Realm.RealmGame
import java.util.*

class DiceManager(var game: RealmGame) {

  private val random = Random()
  private var valueDice1 = 0
  private var valueDice2 = 0
  var sum = 0
  private var currentRule: RealmDice? = null
  private var currentRuleContent: String? = null
  private var ruleListSum: ArrayList<RealmDice> = arrayListOf()
  private var ruleListSingle: ArrayList<RealmDice> = arrayListOf()
  private var titleRule: String? = null

  init {


    this.getRuleFromRealm()

  }


  private fun setupDice(cont: Context) {
    val maniActivity = cont as DiceGameActivity
    val dice1 = maniActivity.findViewById<View>(R.id.simple_anim) as ImageView
    val dice2 = maniActivity.findViewById<View>(R.id.simple_anime) as ImageView
    val ruleTextView = maniActivity.findViewById<View>(R.id.rule) as TextView
    val titleRuleTextView = maniActivity.findViewById<View>(R.id.titleRule) as TextView




    Log.d("Dice1", "$valueDice1")
    Log.d("Dice2", "$valueDice2")
    Log.d("Sum", "$sum")


    val res1 = cont.resources.getIdentifier("dice_$valueDice1", "drawable", cont.packageName)
    val res2 = cont.resources.getIdentifier("dice_$valueDice2", "drawable", cont.packageName)
    ruleTextView.text = currentRuleContent
    titleRuleTextView.text = titleRule
    dice1.setImageResource(res1)
    dice2.setImageResource(res2)
  }

  private fun randomValue() {
    valueDice1 = randomDiceValue()
    valueDice2 = randomDiceValue()
    sum = valueDice1 + valueDice2
  }

  private fun randomDiceValue(): Int {
    return random.nextInt(6) + 1
  }

  private fun getRuleFromRealm() {
    val realmDiceSum = RealmDbHelper().filter<RealmDice>("game_id", game.id, "type", "sum")
    val realmDiceSingle = RealmDbHelper().filter<RealmDice>("game_id", game.id, "type", "single")

    for (dice in realmDiceSum) {
      ruleListSum.add(dice)
    }
    for (dice in realmDiceSingle) {
      ruleListSingle.add(dice)
    }


  }

  private fun bindRuleWithDice() {
    currentRule = ruleListSum.find { it.number == sum }
    currentRuleContent = currentRule!!.content
    val currentDrink = currentRule!!.drink
    if (valueDice1 == valueDice2) {
      currentRuleContent += "\n Distribue $valueDice1 gorgées"
      titleRule = "DOUBLE"
    } else {
      titleRule = null
    }
    if(valueDice1 == 3 && valueDice2 == 3){
      currentRuleContent += "\n Le GROS POULET boit 2 gorgées"
    }else{
      currentRuleContent = "$currentRuleContent"
      currentRuleContent += ruleListSingle.find { it.number == valueDice1 }!!.content
      currentRuleContent = "$currentRuleContent"
      currentRuleContent += ruleListSingle.find { it.number == valueDice2 }!!.content
    }

    currentRuleContent = currentRuleContent!!.replace("%d", currentDrink.toString(), false)

  }

  fun letsGetRandom(cont: Context) {
    this.randomValue()
    this.bindRuleWithDice()
    this.setupDice(cont)
  }

}
