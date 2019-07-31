package com.gambapp.gob.helpers

class TextHelpers {

    fun setFirstCapital(string: String) : String{
        return string.substring(0, 1).toUpperCase() + string.substring(1)
    }
}
