package com.gambapp.gob.helpers

import com.gambapp.gob.R

class ColorHelper {
     fun getColorByType(type: Int): Int? {
        var color : Int? = null
        when (type) {
            0 -> color = R.color.qtype0
            1 -> color = R.color.qtype1
            2 -> color = R.color.qtype2
            3 -> color = R.color.qtype3
            4 -> color = R.color.qtype4
            5 -> color = R.color.qtype5
            6 -> color = R.color.qtype6
            7 -> color = R.color.qtype7
            8 -> color = R.color.qtype8
            9 -> color = R.color.qtype9
            10 -> color = R.color.qtype10
            11 -> color = R.color.qtype10
            12 -> color = R.color.qtype12
            else ->{color = R.color.qtype1}
        }
        return color
    }
}