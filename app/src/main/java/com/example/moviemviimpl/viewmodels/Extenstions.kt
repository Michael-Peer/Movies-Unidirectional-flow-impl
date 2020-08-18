package com.example.moviemviimpl.viewmodels

import com.example.moviemviimpl.model.Genre
import com.example.moviemviimpl.model.ProductionCountry

/**
 *
 * To save some boilerplate code, I decided to write here an Kotlin extension function
 * Extension functions allow us to add functionality to existing classes
 *
 * At start, we declare which class we want to do operation on(List, String, Int etc) - called receiver type
 * In the body on the function, the keyword "this" refer to receiver object
 *
 *        ---About the Generic---
 * By default, the upper bound is Any? instead of Any
 * Hence we need to write explicitly here
 * **/

fun <T> List<T>?.extractWithSepreation(errorString: String): String {
    var text = "There is no available $errorString for this movie"

    if (this.isNullOrEmpty()) {
        return text
    }

    text = ""

    this.forEachIndexed { index, item ->
        when (item) {
            is ProductionCountry -> {
                text += getSepreation(item.name, index)
            }
            is Genre -> {
                text += getSepreation(item.name, index)
            }
        }
    }
    return text
}

fun getSepreation(name: String?, index: Int): String {
    name?.let {
        return if (index == 0) {
            "${name}"
        } else {
            ", ${name}"
        }
    }
    return ""
}

//fun <T> List<T>?.extractClassMemberItemAsList() : List<T>? {
//
//}