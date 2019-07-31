package com.gambapp.gob.services

import android.util.Log
import com.gambapp.gob.model.Card
import com.gambapp.gob.model.Question
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ReportService {
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    init {
        Log.d("Database", "databse init")
    }

    fun reportQuestion(question: Question) {
        Log.d("Database", "report")
        database.child("bug")
                .child("question")
                .child("question- ${question.id}")
                .child(UUID.randomUUID().toString())
                .setValue(question)

    }

    fun reportCard(card: Card) {
        Log.d("Database", "report")
        database.child("bug")
                .child("card")
                .child("card- ${card.id}")
                .child(UUID.randomUUID().toString())
                .setValue(card)

    }
}
