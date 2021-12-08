package com.prime.features.iap.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.vpn.prime.databinding.PrimeOnboardingFragmentBinding

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeOnboardingFragment : Fragment() {

    companion object {
        private const val KEY_IMAGE = "onboarding_image"

        fun newInstance(
            @RawRes animResId: Int,
        ) = PrimeOnboardingFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_IMAGE, animResId)
            }
        }
    }

    private lateinit var binding : PrimeOnboardingFragmentBinding

    private val imageResId: Int
        get() = arguments?.getInt(KEY_IMAGE) ?: 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= PrimeOnboardingFragmentBinding.inflate(inflater, container, false)
        .run {
            binding = this
            root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        image.setAnimation(imageResId)
        image.repeatCount = INFINITE
        image.playAnimation()
    }
}