package com.gambapp.gob.services

import android.util.Log
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.model.Realm.RealmGame
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

val shopsRealmGame: ArrayList<RealmGame?> = arrayListOf()

class ShopsService {
    fun getShopsGame(myCallback: CallBackShops) {
        val rootRef = FirebaseDatabase.getInstance().reference.child("shop")
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as HashMap<*, *>
                val position = mutableListOf(0,1,2,3)
                for (game in map) {
                    shopsRealmGame.add(RealmDbHelper().get<RealmGame>(game.value.toString().toLong()))

                }
                Log.d("tag", map.toString())


                myCallback.onCallback(position,shopsRealmGame)
            }
        })

    }
}

interface CallBackShops {
    fun onCallback(position: MutableList<Int>,games: ArrayList<RealmGame?>)
}