package com.shubh.shubhflix.hilttest

import java.util.Locale
import javax.inject.Inject

class HiltTest @Inject constructor(private val newName: String) {


    fun doWithName(): String {
        return newName.uppercase(Locale.ROOT)
    }
}