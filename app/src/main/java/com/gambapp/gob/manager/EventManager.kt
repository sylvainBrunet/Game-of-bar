package com.gambapp.gob.manager

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class EventManager {


  private lateinit var firebaseAnalytics: FirebaseAnalytics

  fun OnGame(gameId: String, context: Context){
    firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, gameId)

    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
  }
}
