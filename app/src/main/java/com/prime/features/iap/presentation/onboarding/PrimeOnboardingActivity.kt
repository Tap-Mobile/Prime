package com.prime.features.iap.presentation.onboarding

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.lensy.library.extensions.autoCleared
import com.prime.features.iap.presentation.IapScreenName
import com.prime.features.iap.presentation.WelcomePremiumActivity
import com.protonvpn.android.components.ViewPagerAdapter
import com.vpn.prime.R
import com.vpn.prime.databinding.PrimeOnboardingActBinding
import kotlinx.android.synthetic.main.prime_onboarding_act.*
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeOnboardingActivity : WelcomePremiumActivity(), OnPageChangeListener {

    private lateinit var binding: PrimeOnboardingActBinding
    private var adapter by autoCleared<ViewPagerAdapter>()

    private val connectTitle by lazy(NONE) { getString(R.string.prime_onboardingTitleConnection) }
    private val connectDesc by lazy(NONE) { getString(R.string.prime_onboardingDescriptionConnection) }
    private val bandwidthTitle by lazy(NONE) { getString(R.string.prime_onboardingTitleBandwidth) }
    private val bandwidthDesc by lazy(NONE) { getString(R.string.prime_onboardingDescriptionBandwidth) }
    private val locationsTitle by lazy(NONE) { getString(R.string.prime_onboardingTitleLocations) }
    private val locationsDesc by lazy(NONE) { getString(R.string.prime_onboardingDescriptionLocations) }
    private val adsTitle by lazy(NONE) { getString(R.string.prime_onboardingTitleAds) }
    private val adsDesc by lazy(NONE) { getString(R.string.prime_onboardingDescriptionAds) }

    override val btnBack: View
        get() = binding.btnContinueLimited
    override val btnContinue: View
        get() = binding.btnContinue.root
    override val trialInfo: TextView?
        get() = null
    override val premiumScreenName: String
        get() = IapScreenName.SCREEN_WELCOME_ONBOARDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PrimeOnboardingActBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFrag(PrimeOnboardingFragment.newInstance(R.raw.onboarding_connection))
            addFrag(PrimeOnboardingFragment.newInstance(R.raw.onboarding_bandwidth))
            addFrag(PrimeOnboardingFragment.newInstance(R.raw.onboarding_locations))
            addFrag(PrimeOnboardingFragment.newInstance(R.raw.onboarding_ads))
        }

        with(binding) {
            viewPager.adapter = adapter
            viewPager.setInterval(3000)
            viewPager.startAutoScroll(3000)
            indicator.setViewPager(viewPager)
            updateTexts(0)
            viewPager.addOnPageChangeListener(this@PrimeOnboardingActivity)
        }

        initCommonUI()

        startShowCloseButton(DELAY_TO_ALLOW_CLOSE_SCREEN_LONG)
    }

    override fun onResume() {
        super.onResume()
        updateTexts(binding.viewPager.currentItem)
    }

    override fun onPageSelected(position: Int) = updateTexts(position)

    private fun updateTexts(position: Int): Unit = with(binding) {
        title.text = when (position) {
            0 -> connectTitle
            1 -> bandwidthTitle
            2 -> locationsTitle
            3 -> adsTitle
            else -> throw IllegalStateException("Unknown position")
        }
        description.text = when (position) {
            0 -> connectDesc
            1 -> bandwidthDesc
            2 -> locationsDesc
            3 -> adsDesc
            else -> throw IllegalStateException("Unknown position")
        }
    }

    override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) = Unit

    override fun onPageScrollStateChanged(state: Int) = Unit
}
