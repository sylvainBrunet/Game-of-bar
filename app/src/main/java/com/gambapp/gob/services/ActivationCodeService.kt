package com.gambapp.gob.services

import android.content.Context
import android.util.Log
import com.gambapp.gob.MainActivity
import com.gambapp.gob.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tapadoo.alerter.Alerter
import java.security.AccessControlContext
import java.util.*
import java.util.concurrent.TimeUnit


class ActivationCodeService(val context: Context) {
    val cont = context as MainActivity

    fun findCode(myCallback: MyCallback, code: String) {
        if (code.count() != 0) {
            val rootRef = FirebaseDatabase.getInstance().reference.child("code")
            rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(code)) {
                        val id: HashMap<*, *> = (snapshot.child(code).value) as HashMap<*, *>
                        val gameId = id["gameId"] as Long
                        val idString = gameId.toString()
                        val expirationDate = id["date_expiration"]
                        val timeStamp = (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))
                        Log.d("tag", timeStamp.toString())
                        if (timeStamp.toString() < expirationDate.toString()) {
                            val idInt = idString.toInt()
                            if (idInt != 0 && idInt != 1 && idInt != 2) {
                                myCallback.onCallback(idInt.toString(), expirationDate.toString())
                            }
                        } else {
                            Alerter.create(cont)
                                    .setTitle("Trop tard !")
                                    .setText("Le code d'activation est expiré.")
                                    .setBackgroundColorRes(R.color.alert_default_error_background)
                                    .show()
                        }
                    } else {
                        Alerter.create(cont)
                                .setTitle("Erreur !")
                                .setText("Le code n'est pas correct \uD83D\uDE25")
                                .setBackgroundColorRes(R.color.alert_default_error_background)
                                .show()

                    }
                }
            })

        }
    }
}

interface MyCallback {
    fun onCallback(value: String, expirationDate: String)
}
