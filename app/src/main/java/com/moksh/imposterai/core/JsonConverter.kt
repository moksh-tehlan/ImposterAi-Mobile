package com.moksh.imposterai.core

import com.google.gson.Gson

object JsonConverter {
    fun toJson(obj: Any): String {
        val gson = Gson()
        return gson.toJson(obj)
    }

    inline fun <reified T> fromJson(json: String): T {
        val gson = Gson()
        return gson.fromJson(json, T::class.java)
    }
}