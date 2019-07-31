package com.gambapp.gob.helpers

import android.util.Log
import com.gambapp.gob.model.Realm.RealmOwnedGame
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import java.util.concurrent.TimeUnit


class RealmDbHelper {
    val realm = Realm.getDefaultInstance()!!

    inline fun <reified T : RealmModel> get(id: Long): T? =
            realm.where(T::class.java).equalTo("id", id).findFirst()

    inline fun <reified T : RealmModel> getAllSorted(key: String): ArrayList<RealmOwnedGame> {
        realm.refresh()
        val game : RealmResults<RealmOwnedGame> = realm.where(RealmOwnedGame::class.java).findAll().sort(key)
        Log.d("tagGame",game.toString())
        val ownedGame: ArrayList<RealmOwnedGame> = arrayListOf()
        val timeStamp = (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))
        Log.d("tagNow",timeStamp.toString())
        for (g in game){
            if (g.exipiration_date > timeStamp.toString()){
                Log.d("tager",g.game!!.id.toString())
                ownedGame.add(g)
           }else{
                RealmDbHelper().delete<RealmOwnedGame>(g)
            }

        }
        return ownedGame

    }
    inline fun <reified T : RealmModel> getAllArray(): ArrayList<T>{
        val game : RealmResults<T> = realm.where(T::class.java).findAll()
        val games: ArrayList<T> = arrayListOf()

        for (g in game){
            games.add(g)
        }

        return games
    }

    inline fun <reified T : RealmModel> getAll(): RealmResults<T>{


        return realm.where(T::class.java).findAll()
    }


    inline fun <reified T : RealmModel> getByKey(key: String, name: String): T? =
            realm.where(T::class.java).equalTo(key, name).findFirst()

    inline fun <reified T : RealmModel> getByInt(key: String, name: Int): T? =
            realm.where(T::class.java).equalTo(key, name).findFirst()

    inline fun <reified T : RealmModel> checkIfExist(key: String, name: String): T? =
            realm.where(T::class.java).equalTo(key, name).findFirst()




    inline fun <reified T : RealmModel> add(item: RealmObject) {

        realm.let {
            realm.beginTransaction()
            realm.insert(item)
            realm.commitTransaction()
        }


    }

    inline fun <reified T : RealmModel> delete(item: RealmObject) {
        realm.beginTransaction()
        item.deleteFromRealm()
        realm.commitTransaction()

    }

    inline fun <reified T : RealmModel> filter(key1: String, value1: Int, key2: String, value2: String?): RealmResults<T> =
            realm.where(T::class.java).equalTo(key1, value1).and().equalTo(key2, value2).findAll()

    inline fun <reified T : RealmModel> filterOne(key1: String, value1: Int): RealmResults<T> =
            realm.where(T::class.java).equalTo(key1, value1).findAll()

    inline fun <reified T : RealmModel> filterOneString(key1: String, value1: String): RealmResults<T> =
            realm.where(T::class.java).equalTo(key1, value1).findAll()


}
