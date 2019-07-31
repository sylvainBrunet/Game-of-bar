package com.gambapp.gob.helpers

import android.content.Context
import android.widget.Toast

class ToastHelper {

    fun shortToast(context: Context, text: String){
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, text, duration)
        toast.show()
    }
}