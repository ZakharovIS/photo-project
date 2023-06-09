package com.example.unsplashpractice.data

import com.example.unsplashpractice.sharedprefs.TokensStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokensRepository @Inject constructor(
    private val tokensStorage: TokensStorage
) {
    fun addAccessTokenToLocalStorage(token: String?) {
        tokensStorage.putAccessToken(token)
    }

    fun getAccessTokenFromLocalStorage(): String? {
        return tokensStorage.getAccessToken()
    }
}