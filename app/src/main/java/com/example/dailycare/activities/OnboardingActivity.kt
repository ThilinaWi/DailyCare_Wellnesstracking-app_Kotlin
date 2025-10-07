package com.example.dailycare.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.dailycare.R
import com.example.dailycare.databinding.ActivityOnboardingBinding
import com.example.dailycare.fragments.onboarding.OnboardingFragment1
import com.example.dailycare.fragments.onboarding.OnboardingFragment2
import com.example.dailycare.fragments.onboarding.OnboardingFragment3

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
    }
    
    private fun setupViewPager() {
        viewPager = binding.viewPager
        val fragments = listOf(
            OnboardingFragment1(),
            OnboardingFragment2(),
            OnboardingFragment3()
        )
        
        viewPager.adapter = OnboardingPagerAdapter(this, fragments)
        viewPager.isUserInputEnabled = false // Disable swipe navigation
        
        // Set up dots indicator
        binding.dotsIndicator.attachTo(viewPager)
    }
    
    fun nextPage() {
        val currentItem = viewPager.currentItem
        if (currentItem < 2) {
            viewPager.currentItem = currentItem + 1
        }
    }
    
    fun goToAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
    
    private class OnboardingPagerAdapter(
        activity: AppCompatActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(activity) {
        
        override fun getItemCount(): Int = fragments.size
        
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}