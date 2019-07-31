package com.gambapp.gob.manager

import android.content.Context
import android.util.Log
import com.gambapp.gob.R
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.model.Realm.*
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*

open class RealmManager(private var context: Context) {

    private var realm:Realm = Realm.getDefaultInstance()
    private val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    private val currentDate = sdf.format(Date())
    private val farTimestamp = "4102358400"

    fun deleteDatabase() {
        realm.executeTransaction { realm ->
            try {
                realm.delete(RealmCards::class.java)
                realm.delete(RealmGame::class.java)
                realm.delete(RealmPreview::class.java)
                realm.delete(RealmQuestion::class.java)
                realm.delete(RealmDice::class.java)
                realm.delete(RealmOwnedGame::class.java)
                realm.delete((RealmQuestionNumber::class.java))

                Log.d("Realm", "data from database deleted")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Realm", "error ${e.localizedMessage}")
            }

        }
    }
    fun setupDatabase() {
            realm.executeTransaction { realm ->
                try {

                    realm.createAllFromJson(RealmQuestion::class.java, context.resources.openRawResource(R.raw.questions))
                    realm.createAllFromJson(RealmGame::class.java, context.resources.openRawResource(R.raw.games))
                    realm.createAllFromJson(RealmCards::class.java, context.resources.openRawResource(R.raw.cards))
                    realm.createAllFromJson(RealmDice::class.java,context.resources.openRawResource(R.raw.dice))

                    Log.d("Realm", "data loaded by json")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("Realm", "error ${e.localizedMessage}")
                }
        }
    }

    fun setupFreeGame(){
        val freeGame1: RealmGame = RealmDbHelper().get(1)!!
        val freeGame2: RealmGame = RealmDbHelper().get(2)!!
        val freeGame3: RealmGame = RealmDbHelper().get(4)!!

        val ownedGame1 = RealmOwnedGame()
        val ownedGame2 = RealmOwnedGame()
        val ownedGame3 = RealmOwnedGame()

        ownedGame1.game = freeGame1
        ownedGame1.acquisition_date = currentDate
        ownedGame1.exipiration_date = farTimestamp
        ownedGame1.acquisition_code = "ALWAYS_FREE"
        ownedGame1.position = 99

        ownedGame2.game = freeGame2
        ownedGame2.acquisition_date = currentDate
        ownedGame2.exipiration_date = farTimestamp
        ownedGame2.acquisition_code = "ALWAYS_FREE"
        ownedGame2.position = 99


        ownedGame3.game = freeGame3
        ownedGame3.acquisition_date = currentDate
        ownedGame3.exipiration_date = farTimestamp
        ownedGame3.acquisition_code = "ALWAYS_FREE"
        ownedGame3.position = 99


        RealmDbHelper().add<RealmOwnedGame>(ownedGame1)
        RealmDbHelper().add<RealmOwnedGame>(ownedGame2)
        RealmDbHelper().add<RealmOwnedGame>(ownedGame3)

    }

    fun addOwnedGame(gameId: Int, code: String, expirationDate: String?){

        if(alreadyBought(gameId)){

        }else{
            val newGameBought: RealmGame? = RealmDbHelper().get(gameId.toLong())
                if(newGameBought != null){

                val boughtGame = RealmOwnedGame()
                boughtGame.game = newGameBought
                boughtGame.acquisition_date = currentDate
                if (expirationDate == null){
                    boughtGame.exipiration_date = farTimestamp

                }else{
                    boughtGame.exipiration_date = expirationDate

                }
                boughtGame.acquisition_code = code
                boughtGame.position = 1
                RealmDbHelper().add<RealmOwnedGame>(boughtGame)
            }


        }
    }
     fun alreadyBought(gameId: Int) : Boolean{
        var alreadyBought = false
        val newGameBought: RealmOwnedGame? = RealmDbHelper().getByInt("game.id",gameId)
        if (newGameBought != null){
            Log.d("alreadyBought",newGameBought.toString())
            alreadyBought = true
        }
        return alreadyBought

    }
}

//open class RealmManager(private var context: Context) {
//
//    private var realm: Realm = Realm.getDefaultInstance()
//    private val sharedPreferences: PreferenceHelper = PreferenceHelper
//    private val localVersion = sharedPreferences.getIntegerPreference(context,"version", 0)
//    private val currentAppVersion: Int = 1
//
//    fun setupDatabase() {
//        if(localVersion != currentAppVersion){
//            realm.executeTransaction { realm ->
//                try {
//                    realm.delete(RealmCards::class.java)
//                    realm.delete(RealmGame::class.java)
//                    realm.delete(RealmQuestion::class.java)
//                    realm.delete(RealmDice::class.java)
//                    realm.delete((RealmQuestionNumber::class.java))
//                    realm.createAllFromJson(RealmQuestion::class.java, context.resources.openRawResource(R.raw.questions))
//                    realm.createAllFromJson(RealmGame::class.java, context.resources.openRawResource(R.raw.games))
//                    realm.createAllFromJson(RealmCards::class.java, context.resources.openRawResource(R.raw.cards))
//                    realm.createAllFromJson(RealmDice::class.java,context.resources.openRawResource(R.raw.dice))
//
//                    Log.d("Realm", "data loaded by json")
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Log.d("Realm", "error ${e.localizedMessage}")
//                }
//            }
//            sharedPreferences.setIntegerPreference(context,"version",currentAppVersion)
//        }
//
//    }
//}