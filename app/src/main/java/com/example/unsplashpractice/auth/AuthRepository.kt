package com.example.unsplashpractice.auth

import android.util.Log
import com.example.unsplashpractice.data.TokensRepository
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val tokensRepository: TokensRepository
) {

    fun corruptAccessToken() {
        TokenStorage.accessToken = "fake token"
    }

    fun logout() {
        TokenStorage.accessToken = null
        TokenStorage.refreshToken = null
        TokenStorage.idToken = null
        tokensRepository.addAccessTokenToLocalStorage(null)

    }

    fun getCurrentToken(): String? {
        return tokensRepository.getAccessTokenFromLocalStorage()
    }

    fun getAuthRequest(): AuthorizationRequest {
        return AppAuth.getAuthRequest()
    }

    fun getEndSessionRequest(): EndSessionRequest {
        return AppAuth.getEndSessionRequest()
    }

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ) {
        val tokens = AppAuth.performTokenRequestSuspend(authService, tokenRequest)
        //обмен кода на токен произошел успешно, сохраняем токены и завершаем авторизацию

        tokensRepository.addAccessTokenToLocalStorage(tokens.accessToken)

        /*TokenStorage.accessToken = tokens.accessToken
        TokenStorage.refreshToken = tokens.refreshToken
        TokenStorage.idToken = tokens.idToken*/
        Log.d(
            "Oauth",
            "6. Tokens accepted:\n access=${tokens.accessToken}\nrefresh=${tokens.refreshToken}\nidToken=${tokens.idToken}"
        )
    }
}
