package com.example.unsplashpractice.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.unsplashpractice.MainActivity
import com.example.unsplashpractice.databinding.ActivityAuthorizationBinding
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

@AndroidEntryPoint
class AuthorizationActivity : AppCompatActivity() {

    private val viewModel: AuthorizationViewModel by viewModels()
    private lateinit var binding: ActivityAuthorizationBinding

    private val getAuthResponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val dataIntent = it.data ?: return@registerForActivityResult
        handleAuthResponseIntent(dataIntent)
    }

    private val logoutResponse = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        Log.d("Oauth","10. Logout result = $it")
        //Чистим кэш глайда
        runCatching {
            Glide.get(applicationContext).clearDiskCache()
        }
        viewModel.webLogoutComplete()
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindViewModel()
    }

    private fun bindViewModel() {
        binding.authorizationButton.setOnClickListener { viewModel.openLoginPage() }

        viewModel.loadingFlow.launchAndCollectIn(this) {
            updateIsLoading(it)
        }
        viewModel.openAuthPageFlow.launchAndCollectIn(this) {
            openAuthPage(it)
        }
        viewModel.toastFlow.launchAndCollectIn(this) {
            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()

        }
        viewModel.authSuccessFlow.launchAndCollectIn(this) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        viewModel.logoutPageFlow.launchAndCollectIn(this) {
            logoutResponse.launch(it)
        }

        viewModel.logoutCompletedFlow.launchAndCollectIn(this) {
            val intent = intent
            finish()
            startActivity(intent)
            Log.d("Oauth","11. Logout complete")
        }
    }

    private fun updateIsLoading(isLoading: Boolean) = with(binding) {
        authorizationButton.isVisible = !isLoading
        loginProgress.isVisible = isLoading
    }

    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        // пытаемся получить ошибку из ответа. null - если все ок
        val exception = AuthorizationException.fromIntent(intent)
        // пытаемся получить запрос для обмена кода на токен, null - если произошла ошибка
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            // авторизация завершались ошибкой
            exception != null -> viewModel.onAuthCodeFailed(exception)
            // авторизация прошла успешно, меняем код на токен
            tokenExchangeRequest != null ->
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }
}