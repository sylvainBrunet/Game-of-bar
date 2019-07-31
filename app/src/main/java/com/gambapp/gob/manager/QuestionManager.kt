package com.gambapp.gob.manager

import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.helpers.TextHelpers
import com.gambapp.gob.model.Question
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.model.Realm.RealmPlayer
import com.gambapp.gob.model.Realm.RealmQuestion
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList


class QuestionManager(var game: RealmGame) {

    private val diffBetweenRules: Int = 15
    private var numberOfRules: Int = 0
    private val questions: CopyOnWriteArrayList<RealmQuestion> = CopyOnWriteArrayList()
    private val rules: CopyOnWriteArrayList<RuleStruct> = CopyOnWriteArrayList()
    private val finalQuestions: CopyOnWriteArrayList<Question> = CopyOnWriteArrayList()
    private val random = Random()
    private val players = RealmDbHelper().getAll<RealmPlayer>()


    init {
        players.let {
            this.getQuestions()
            this.questions.shuffle()

            numberOfRules = this.questions.count() / this.diffBetweenRules - 1
            if (game.id != 9){
                this.getRules()
                this.rules.shuffle()
                this.bindRulesWithQuestions()
            }

            this.setupFinalQuestions()
            this.setPlayer()
        }
    }

    private fun getQuestions() {
        val counter = game.questions_number.count()
        for (i in 0 until counter) {
            val questionNumberObject = game.questions_number[i]
            val questionsTmp: CopyOnWriteArrayList<RealmQuestion> =
                    this.dbRequestRealmQuestion(questionNumberObject!!.type, questionNumberObject.number)
            for (question in questionsTmp) {
                this.questions.add(question)
            }
        }
    }

    private fun getRules() {
        val rulesTmp: CopyOnWriteArrayList<RealmQuestion> = this.dbRequestRealmQuestion(2, numberOfRules)
        for (rule in rulesTmp) {
            val parentKey = rule.key

            rule.key.let {
                val endQuestion = RealmDbHelper()
                        .filter<RealmQuestion>("type", 2, "parent_key", parentKey).first()
                val ruleObject = RuleStruct(rule, endQuestion!!, random.nextInt(7..11))
                this.rules.add(ruleObject)
            }
        }
    }

    private fun bindRulesWithQuestions() {
        var index = 0
        for (e in this.questions) {
            if (index % this.diffBetweenRules == 0 && index != 0) {
                if (this.rules.count() != 0) {
                    val rule = this.rules.first()
                    rule.let {
                        val endPosition = index + rule.diff
                        this.questions.add(this.questions[index])
                        this.questions.add(this.questions[endPosition])
                        this.questions[index] = rule.begin
                        this.questions[endPosition] = rule.end
                        this.rules.removeAt(0)
                    }
                }
            }
            index += 1
        }
    }

    private fun dbRequestRealmQuestion(typeParam: Int, numberParam: Int): CopyOnWriteArrayList<RealmQuestion> {
        val arrayTarget: CopyOnWriteArrayList<RealmQuestion> = CopyOnWriteArrayList()
        val questionsTmp = RealmDbHelper()
                .filter<RealmQuestion>("type", typeParam, "parent_key", null)
        val temporaryArrayListOfQuestions: CopyOnWriteArrayList<RealmQuestion> = CopyOnWriteArrayList()
        for (q in questionsTmp){
            temporaryArrayListOfQuestions.add(q)
        }
        var questionNumber = numberParam
        if (temporaryArrayListOfQuestions.count() < questionNumber) {
            questionNumber = temporaryArrayListOfQuestions.count()
        }


        for (i in questionNumber downTo 1) {
            val randomIndex = random.nextInt(0 until temporaryArrayListOfQuestions.count())
            arrayTarget.add(temporaryArrayListOfQuestions[randomIndex]!!)
            temporaryArrayListOfQuestions.removeAt(randomIndex)
        }
        return arrayTarget
    }

    private fun setupFinalQuestions() {
        for (question in this.questions) {
            val questionEnd = Question(question)
            this.finalQuestions.add(questionEnd)
            if (question.type == 8) {
                val key = question.key
                key.let {
                    val question_type = question.type
                    val dbResult = RealmDbHelper()
                            .filter<RealmQuestion>("type", question_type, "parent_key", key)
                    val endQuestion = dbResult.first()
                    val questionFinal = Question(endQuestion!!)
                    this.finalQuestions.add(questionFinal)
                }
            }
        }
    }

    private fun setPlayer() {
        var currentPlayerInRule: RealmPlayer? = null
        for (e in 0 until this.finalQuestions.count()) {
            val playerTmp = ArrayList<RealmPlayer>()
            for (p in this.players) {
                playerTmp.add(p)
            }
            val question = this.finalQuestions[e]
            val playerIndex1 = (0 until playerTmp.count()).random()
            var player1 = playerTmp[playerIndex1]

            val playerIndex2 = if (playerTmp.count() == 1) {
                0
            } else {
                playerTmp.removeAt(playerIndex1)
                (0 until playerTmp.count()).random()
            }

            if (question.parent_key != null) {

                player1 = currentPlayerInRule!!
                currentPlayerInRule = null
            }
            val player2 = playerTmp[playerIndex2]
            var dummy = this.finalQuestions[e].content

            val player1Caps = TextHelpers().setFirstCapital(player1.player_name!!)
            val player2Caps = TextHelpers().setFirstCapital(player2.player_name!!)



            dummy = dummy
                    .replace("%d", question.drink.toString(), false)
                    .replace("%p", player1Caps, false)
                    .replace("%o", player2Caps, false)
            this.finalQuestions[e].content = dummy
            if ( question.key != null && question.type == 2 ) {
                currentPlayerInRule = player1
            }
        }

    }

    fun getFinalQuestions(): CopyOnWriteArrayList<Question> {
        return this.finalQuestions
    }

    fun removeFirstQuestionInFinalQuestions() {
        this.finalQuestions.removeAt(0)
    }

    private fun IntRange.random() = (Math.random() * ((endInclusive + 1) - start) + start).toInt()

    private fun Random.nextInt(range: IntRange): Int {

        return range.start + nextInt(range.last - range.start)
    }


}

class RuleStruct(val begin: RealmQuestion = RealmQuestion(),
                 val end: RealmQuestion = RealmQuestion(),
                 val diff: Int = 0)
