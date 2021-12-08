package com.prime.features.iap.presentation

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.vpn.prime.databinding.PrimeIapActBinding

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeIapActivity : BasePremiumActivity() {

    private lateinit var binding: PrimeIapActBinding

    override val btnBack: View
        get() = binding.btnContinueLimited
    override val btnContinue: View
        get() = binding.btnContinue.root
    override val trialInfo: TextView
        get() = binding.trialInfoPremium
    override val premiumScreenName: String
        get() = IapScreenName.SCREEN_WELCOME_ONBOARDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PrimeIapActBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCommonUI()

        startShowCloseButton(DELAY_TO_ALLOW_CLOSE_SCREEN_SHORT)
    }

    override fun onSubClicked() = subscribe()
}
