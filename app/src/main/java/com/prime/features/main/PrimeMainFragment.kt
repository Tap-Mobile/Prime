package com.prime.features.main

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.jakewharton.rxrelay2.PublishRelay
import com.lensy.library.extensions.onBackPressed
import com.lensy.library.extensions.visible
import com.prime.features.main.model.MainSelectedCountry
import com.prime.features.main.model.VpnAnimation
import com.prime.features.main.model.VpnStateUI
import com.prime.features.main.model.asTransition
import com.prime.features.navigation.NavigationHelper
import com.prime.utils.ViewAnimations
import com.protonvpn.android.utils.ViewModelFactory
import com.vpn.prime.R
import com.vpn.prime.databinding.PrimeMainFragBinding
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Developed by
 * @author Aleksandr Artemov
 */
private data class MainUiAnimState(val state: VpnStateUI?, val prevState: VpnStateUI?, val animation: VpnAnimation?) {
    companion object {
        fun fromScratch() = MainUiAnimState(null, null, null)
    }
}

class PrimeMainFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: PrimeMainViewModel by viewModels { viewModelFactory }
    private lateinit var binding: PrimeMainFragBinding

    private val ctaTextConnect by lazy(NONE) { getString(R.string.prime_mainBtnConnect) }
    private val ctaTextConnecting by lazy(NONE) { getString(R.string.prime_mainBtnConnecting) }
    private val ctaTextDisconnect by lazy(NONE) { getString(R.string.prime_mainBtnDisconnect) }
    private val ctaTextDisconnecting by lazy(NONE) { getString(R.string.prime_mainBtnDisconnecting) }
    private val ctaTextWaitingNetwork by lazy(NONE) { getString(R.string.prime_mainBtnWaitingNetwork) }
    private val ctaTextLoadingServers by lazy(NONE) { getString(R.string.prime_mainBtnLoadingServers) }

    private val ctaBgColored by lazy(NONE) {
        ContextCompat.getDrawable(requireContext(), R.drawable.prime_main_bg_btn_connect)!!
    }
    private val ctaBgBlack by lazy(NONE) {
        ContextCompat.getDrawable(requireContext(), R.drawable.prime_main_bg_btn_disconnect)!!
    }
    private val bounceAnim by lazy(NONE) { resources.getDimension(R.dimen.prime_mainTipBounceAnim) }

    private val ctaTextColorMain by lazy(NONE) { ContextCompat.getColor(requireContext(), R.color.primeColorPrimary) }
    private val ctaTextColorBlack by lazy(NONE) {
        ContextCompat.getColor(requireContext(), R.color.primeColorMainBtnDisconnect)
    }

    private val compositeDisposable = CompositeDisposable()
    private val showTipRelay: PublishRelay<Boolean> = PublishRelay.create<Boolean>()
    private val currentAnimationRelay: PublishRelay<VpnAnimation> = PublishRelay.create<VpnAnimation>()

    private var doubleBackToExitPressedOnce = false
    private var tipBounceAnimator: ObjectAnimator? = null
    private var tipFadeInAnim: Animation? = null

    private var lastUiDrawnState = MainUiAnimState.fromScratch()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PrimeMainFragBinding.inflate(inflater, container, false)
        .run {
            binding = this
            root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        lastUiDrawnState = MainUiAnimState.fromScratch()
        onBackPressed { closeScreen() }

        imageStatus.setOnClickListener { viewModel.onConnectClicked() }
        btnConnect.setOnClickListener { viewModel.onConnectClicked() }

        btnMenu.setOnClickListener {
            findNavController().navigate(PrimeMainFragmentDirections.openMenu())
        }

        btnLocation.setOnClickListener {
            findNavController().navigate(PrimeMainFragmentDirections.openServers())
        }

        btnGoPremium.root.setOnClickListener {
            NavigationHelper.startIapScreen(this@PrimeMainFragment)
        }

        btnInfo.setOnClickListener {
            Toast.makeText(activity, "info clicked", Toast.LENGTH_SHORT).show()
        }

        if (imageStatusStub.visible) {
            imageStatus.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    imageStatusStub.visible = false
                    imageStatus.removeAnimatorListener(this)
                }

                override fun onAnimationEnd(animation: Animator?) = Unit

                override fun onAnimationCancel(animation: Animator?) = Unit

                override fun onAnimationRepeat(animation: Animator?) = Unit
            })
        }

        viewModel.let { vm ->
            viewModel.requestUpdateSelectedCountry()
            vm.serverManager.updateEvent.observe(viewLifecycleOwner) { viewModel.onServerUpdated() }
            vm.userData.updateEvent.observe(viewLifecycleOwner) { viewModel.requestUpdateSelectedCountry() }

            vm.userData.premiumLiveData.observe(viewLifecycleOwner, Observer { updatePremiumUI(it) })
            vm.isBlockUI.observe(viewLifecycleOwner, Observer { updateBlockUI(it) })
            vm.vpnStatus.observe(viewLifecycleOwner, Observer { updateVpnState(it) })
            vm.isShowTip.observe(viewLifecycleOwner, Observer { showTipRelay.accept(it) })
            vm.selectedCountry.observe(viewLifecycleOwner, Observer { updateSelectedCountry(it) })
        }

        compositeDisposable.add(
            Observable.combineLatest(
                currentAnimationRelay,
                showTipRelay,
                BiFunction { anim: VpnAnimation, showTip: Boolean ->
                    showTip && anim == VpnAnimation.State.Idle
                })
                .subscribe { updateTip(it) }
        )
    }

    // override fun onResume() {
    //     super.onResume()
    //
    //     if (viewModel.adsManager.isAdLoaded && viewModel.vpnStateMonitor.isDisabled) {
    //         viewModel.adsManager.show(false, requireActivity())
    //     }
    // }

    private fun updateBlockUI(isBlocked: Boolean): Unit = with(binding) {
        arrayOf(btnGoPremium.root, btnConnect, btnLocation, btnMenu, btnInfo, imageStatus).forEach {
            it.isEnabled = isBlocked.not()
        }
    }

    private fun startTipAnimation(): Unit = with(binding) {
        if (tipBounceAnimator?.isRunning == true) return
        stopTipAnimation()
        tipBounceAnimator = ViewAnimations.makeBounceAnim(imageStatusTip, 500, 0f, -bounceAnim)
        tipFadeInAnim = ViewAnimations.fadeIn(imageStatusTip, 300, 4000)
    }

    private fun stopTipAnimation(): Unit = with(binding) {
        ViewAnimations.stopAnim(tipBounceAnimator)
        ViewAnimations.stopAnim(tipFadeInAnim)
        tipBounceAnimator = null
        tipFadeInAnim = null

        imageStatusTip.visible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTipAnimation()
        compositeDisposable.clear()
    }

    private fun updatePremiumUI(isPremium: Boolean) = with(binding) {
        btnGoPremium.root.visible = !isPremium
    }

    private fun updateTip(isEnabled: Boolean) {
        if (isEnabled) startTipAnimation()
        else stopTipAnimation()
    }

    private fun updateSelectedCountry(country: MainSelectedCountry): Unit = with(binding) {
        when (country) {
            MainSelectedCountry.OptimalLocation -> {
                serverOptimal.root.visible = true
                serverCountry.root.visible = false
            }
            is MainSelectedCountry.Country -> {
                serverOptimal.root.visible = false
                serverCountry.root.visible = true

                serverCountry.countryTitle.text = country.name

                Glide.with(serverCountry.countryLocation)
                    .load(country.flagImage)
                    .circleCrop()
                    .placeholder(R.drawable.prime_servers_bg_flag)
                    .into(serverCountry.countryLocation)
            }
        }
    }

    private fun updateVpnState(newState: VpnStateUI) = with(binding) {
        val currentVpnState = lastUiDrawnState.state
        val prevVpnState = lastUiDrawnState.prevState
        val currentVpnAnim = lastUiDrawnState.animation

        Timber.d("new=${newState} current=$currentVpnState prev=$prevVpnState")
        if (currentVpnState == newState) return

        val animation = when (newState) {
            VpnStateUI.Disabled -> when (currentVpnState) {
                VpnStateUI.Disconnecting ->
                    if (prevVpnState == VpnStateUI.Connected) VpnAnimation.Transition.Disconnecting
                    else VpnAnimation.State.Idle
                else -> VpnAnimation.State.Idle
            }

            VpnStateUI.Connecting -> VpnAnimation.State.Loading

            VpnStateUI.Connected -> when (currentVpnState) {
                null -> VpnAnimation.State.Connected
                else -> VpnAnimation.Transition.Connecting
            }

            VpnStateUI.Disconnecting -> VpnAnimation.Skip

            is VpnStateUI.Error -> VpnAnimation.State.Error
            VpnStateUI.WaitingForNetwork -> VpnAnimation.State.Error
            VpnStateUI.LoadingServers -> VpnAnimation.State.Loading
        }

        if (animation != currentVpnAnim) {
            updateImageStatus(animation)
        }
        updateCtaButtonText(newState, prevVpnState)

        lastUiDrawnState = MainUiAnimState(newState, currentVpnState, animation)
    }

    private data class BtnConnectUpdate(val text: String, val bg: Drawable, val color: Int)

    private fun updateCtaButtonText(state: VpnStateUI, prevState: VpnStateUI?): Unit = with(binding.btnConnect) {
        val update = when (state) {
            VpnStateUI.LoadingServers -> BtnConnectUpdate(ctaTextLoadingServers, ctaBgColored, ctaTextColorMain)
            VpnStateUI.Connected -> BtnConnectUpdate(ctaTextDisconnect, ctaBgBlack, ctaTextColorBlack)
            VpnStateUI.Connecting -> BtnConnectUpdate(ctaTextConnecting, ctaBgColored, ctaTextColorMain)
            VpnStateUI.Disabled,
            is VpnStateUI.Error -> BtnConnectUpdate(ctaTextConnect, ctaBgColored, ctaTextColorMain)
            VpnStateUI.WaitingForNetwork -> BtnConnectUpdate(ctaTextWaitingNetwork, ctaBgColored, ctaTextColorMain)
            VpnStateUI.Disconnecting ->
                if (prevState != VpnStateUI.Connected && text.isNullOrEmpty().not())
                    null
                else
                    BtnConnectUpdate(ctaTextDisconnecting, ctaBgBlack, ctaTextColorBlack)
        }

        update?.let {
            text = it.text
            setTextColor(it.color)
            background = it.bg
        }
    }

    private fun updateImageStatus(vpnAnimation: VpnAnimation): Unit = with(binding) {
        Timber.i("updateImageStatus: $vpnAnimation")

        if (vpnAnimation is VpnAnimation.Skip) return


        if (vpnAnimation.isTransition) {
            imageStatus.addAnimatorListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(animation: Animator?) = Unit

                override fun onAnimationEnd(animation: Animator?) {
                    imageStatus.removeAnimatorListener(this)
                    updateImageStatus(vpnAnimation.asTransition().nextState)
                }

                override fun onAnimationCancel(animation: Animator?) = Unit

                override fun onAnimationRepeat(animation: Animator?) = Unit
            })
        }
        imageStatus.setMinAndMaxFrame(vpnAnimation.frames.min, vpnAnimation.frames.max)
        imageStatus.speed = if (vpnAnimation is VpnAnimation.Transition.Disconnecting) 2f else 1f
        imageStatus.repeatCount =
            if (vpnAnimation.isTransition) 0 else com.airbnb.lottie.LottieDrawable.INFINITE
        imageStatus.playAnimation()
        currentAnimationRelay.accept(vpnAnimation)
    }

    private fun closeScreen() {
        if (doubleBackToExitPressedOnce) {
            activity?.moveTaskToBack(true)
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(context, R.string.clickBackAgainLogout, Toast.LENGTH_LONG).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}