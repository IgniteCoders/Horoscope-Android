package com.ignite.horoscope.utils

import android.content.Context
import android.content.SharedPreferences


class SessionManager(context: Context) {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("horoscope_session", Context.MODE_PRIVATE)


    fun setFavorite(horoscopeId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("FAVORITE_HOROSCOPE_ID", horoscopeId)
        editor.apply()
    }

    fun getFavorite(): String {
        return sharedPreferences.getString("FAVORITE_HOROSCOPE_ID", "")!!
    }

    fun isFavorite(horoscopeId: String): Boolean {
        return horoscopeId == getFavorite()
    }
}