package com.example.unsplashpractice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.unsplashpractice.R.id
import com.example.unsplashpractice.auth.TokenStorage
import com.example.unsplashpractice.data.TokensRepository
import com.example.unsplashpractice.databinding.ActivityMainBinding
import com.example.unsplashpractice.onboarding.OnboardingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var tokensRepository: TokensRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Log.d("token", "${tokensRepository.getAccessTokenFromLocalStorage()}")

        if(tokensRepository.getAccessTokenFromLocalStorage() == null) {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
        } else {
            TokenStorage.accessToken = tokensRepository.getAccessTokenFromLocalStorage()
        }
        Log.d("token", "${TokenStorage.accessToken}")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                id.navigation_photos, id.navigation_collections, id.navigation_user
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        /*if(Intent.ACTION_VIEW.equals(intent.action) && intent.data != null) {
            val photoID = intent.data!!.lastPathSegment
            if(photoID != null) {
                val action = PhotosFragmentDirections.actionNavigationPhotosToNavigationPhotoDetail(
                    photoID
                )
                navController.navigate(action)
            }

        }*/






    }

    //Для работы кнопки назад в ActionBar.
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}