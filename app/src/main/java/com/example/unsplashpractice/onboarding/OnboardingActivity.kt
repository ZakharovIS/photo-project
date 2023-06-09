package com.example.unsplashpractice.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unsplashpractice.R
import com.example.unsplashpractice.auth.AuthorizationActivity
import com.example.unsplashpractice.databinding.ActivityOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnboardingItems()
    }

    private fun setOnboardingItems() {
        onboardingAdapter = OnboardingAdapter(
            listOf(
                getString(R.string.onboarding_1),
                getString(R.string.onboarding_2),
                getString(R.string.onboarding_3)
            )
        )
        val onboardingViewPager = binding.onboardingViewPager
        onboardingViewPager.adapter = onboardingAdapter
        binding.arrowRight.setOnClickListener {
            if(onboardingViewPager.currentItem + 1 < onboardingAdapter.itemCount) {
                onboardingViewPager.currentItem += 1
            } else {
                startActivity(Intent(this, AuthorizationActivity::class.java))
                finish()
            }
        }
        binding.arrowLeft.setOnClickListener {
            if(onboardingViewPager.currentItem > 0) {
                onboardingViewPager.currentItem -= 1
            }
        }
    }
}