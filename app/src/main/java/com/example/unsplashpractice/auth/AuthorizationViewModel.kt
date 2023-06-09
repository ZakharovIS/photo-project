package com.example.unsplashpractice.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplashpractice.R
import com.example.unsplashpractice.data.models.UnsplashPhotosDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val authRepository: AuthRepository,
    private val dbRepository: UnsplashPhotosDatabaseRepository
) : ViewModel() {

    private val authService: AuthorizationService = AuthorizationService(applicationContext)

    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)
    private val logoutPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val logoutCompletedEventChannel = Channel<Unit>(Channel.BUFFERED)
    private val loadingMutableStateFlow = MutableStateFlow(false)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()

    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val authSuccessFlow: Flow<Unit>
        get() = authSuccessEventChannel.receiveAsFlow()

    val logoutPageFlow: Flow<Intent>
        get() = logoutPageEventChannel.receiveAsFlow()

    val logoutCompletedFlow: Flow<Unit>
        get() = logoutCompletedEventChannel.receiveAsFlow()

    init {
        if (authRepository.getCurrentToken() != null) {
            logout()
        }
    }


    fun onAuthCodeFailed(exception: AuthorizationException) {
        toastEventChannel.trySendBlocking(R.string.auth_canceled)
    }

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {

        Log.d("Oauth","3. Received code = ${tokenRequest.authorizationCode}")

        viewModelScope.launch {
            loadingMutableStateFlow.value = true
            runCatching {
                Log.d("Oauth","4. Change code to token. Url = ${tokenRequest.configuration.tokenEndpoint}, verifier = ${tokenRequest.codeVerifier}")
                authRepository.performTokenRequest(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                loadingMutableStateFlow.value = false
                authSuccessEventChannel.send(Unit)
            }.onFailure {
                loadingMutableStateFlow.value = false
                toastEventChannel.send(R.string.auth_canceled)
            }
        }
    }

    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val authRequest = authRepository.getAuthRequest()

        Log.d("Oauth","1. Generated verifier=${authRequest.codeVerifier},challenge=${authRequest.codeVerifierChallenge}")

        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRequest,
            customTabsIntent
        )

        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)
        Log.d("Oauth","2. Open auth page: ${authRequest.toUri()}")
    }

    fun logout() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val logoutPageIntent = authService.getEndSessionRequestIntent(
            authRepository.getEndSessionRequest(),
            customTabsIntent
        )
        deletePhotos()
        authRepository.logout()
        logoutPageEventChannel.trySendBlocking(logoutPageIntent)
        Log.d("Oauth","9. Logout")
    }

    fun deletePhotos() {
        viewModelScope.launch {
            dbRepository.deleteAllDb()
        }
    }

    fun webLogoutComplete() {
        authRepository.logout()
        logoutCompletedEventChannel.trySendBlocking(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}
