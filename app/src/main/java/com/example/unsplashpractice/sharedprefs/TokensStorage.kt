package com.example.unsplashpractice.sharedprefs

import android.content.Context
import android.content.Context.MODE_PRIVATE


class TokensStorage (context: Context) {
    private val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
    private val editor = prefs.edit()

    fun putAccessToken(accessToken: String?) {

        editor.putString(ACCESS_TOKEN, accessToken)
        editor.apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN, null)

    }

    companion object {
        private const val ACCESS_TOKEN = "requests_set"
        private const val PREFERENCE_NAME = "pref_name"
    }
}