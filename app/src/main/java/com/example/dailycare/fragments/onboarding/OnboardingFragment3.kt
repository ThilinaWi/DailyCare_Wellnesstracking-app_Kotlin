package com.example.dailycare.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailycare.activities.OnboardingActivity
import com.example.dailycare.databinding.FragmentOnboarding3Binding

class OnboardingFragment3 : Fragment() {
    
    private var _binding: FragmentOnboarding3Binding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding3Binding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnSignIn.setOnClickListener {
            (activity as OnboardingActivity).goToAuth()
        }
        
        binding.btnSignUp.setOnClickListener {
            (activity as OnboardingActivity).goToAuth()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}