package com.prime.features.iap.presentation

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.github.johnpersano.supertoasts.library.SuperToast
import com.lensy.library.extensions.enableFullScreen
import com.prime.data.AppCrashlytics
import com.prime.features.navigation.data.NavigationAnalytics
import com.prime.ui.base.PrimeBaseActivity
import com.prime.utils.ViewAnimations
import com.vpn.prime.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

object IapScreenName {
    const val SCREEN_WELCOME_ONBOARDING = "welcome_onboarding"
}

abstract class BasePremiumActivity : PrimeBaseActivity() {
    companion object {
        const val DELAY_TO_ALLOW_CLOSE_SCREEN_SHORT: Long = 2500
        const val DELAY_TO_ALLOW_CLOSE_SCREEN_LONG: Long = 4000
    }

    @Inject lateinit var navigationAnalytics: NavigationAnalytics

    private val btnCloseTopMargin by lazy(LazyThreadSafetyMode.NONE) { resources.getDimension(R.dimen.primeIapBtnCloseTop) }

    private var loadingDialog: ProgressDialog? = null
    abstract val btnBack: View?
    abstract val btnContinue: View?
    abstract val trialInfo: TextView?

    private var initialization: Disposable? = null
    private var showCloseButton: Disposable? = null
    private var errorToast: SuperToast? = null
    private var subscribing: Disposable? = null
    protected val compositeDisposable = CompositeDisposable()

    open val isNeedToFixCutout = false
    private var isCommonUiInitialized = false
    protected var blockCloseFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationAnalytics.logIapScreen(premiumScreenName)
    }

    override fun onStart() {
        super.onStart()
        if (!isCommonUiInitialized) throw RuntimeException("Don't forget to call 'initCommonUI' in onCreate")
    }

    override fun onResume() {
        super.onResume()

        enableFullScreen()
        if (isNeedToFixCutout) {
            btnBack?.let { it.post { CutoutUtils.fixViewTopCutout(it, window, btnCloseTopMargin) } }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopShowCloseButton()
        compositeDisposable.clear()
    }

    private fun stopShowCloseButton() {
        if (showCloseButton != null && !showCloseButton!!.isDisposed) {
            showCloseButton!!.dispose()
            showCloseButton = null
        }
    }

    protected abstract val premiumScreenName: String

    protected abstract fun onSubClicked()

    protected open fun hasSubInfo(): Boolean = true

    fun initCommonUI() {
        btnContinue?.setOnClickListener { onSubClicked() }
        btnBack?.setOnClickListener { onBackPressed() }

        isCommonUiInitialized = true
    }
    private fun showLoadingMessage(message: Int) {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.setMessage(getString(message))
        } else {
            loadingDialog = ProgressDialog(this)
            loadingDialog!!.setCancelable(false)
            loadingDialog!!.setMessage(getString(message))
            loadingDialog!!.show()
        }
    }

    private fun stopLoading() {
        if (isLiveActivity && loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
            loadingDialog = null
        }
    }

    protected fun startShowCloseButton(delayToAllowCloseScreen: Long) {
        btnBack?.let {
            it.visibility = View.INVISIBLE
            blockCloseFlag = true
            showCloseButton = Single.just(0)
                .delay(delayToAllowCloseScreen, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .ignoreElement()
                .subscribe(
                    { unblockClose() },
                    { error ->
                        AppCrashlytics.recordException(error)
                        unblockClose()
                    }
                )
        }
    }

    private fun unblockClose() {
        btnBack?.let {
            if (!isFinishing && it.visibility != View.VISIBLE) {
                ViewAnimations.fadeIn(it, 300)
            }
        }
        blockCloseFlag = false
    }

    override fun onBackPressed() {
        if (!blockCloseFlag) {
            setResult(RESULT_OK, Intent())
            finish()
        }
    }

    protected fun subscribe() {
        subscribeTo()
    }

    protected open fun subscribeTo() {
        if (subscribing?.isDisposed?.not() == true) return

        handleSubscribed()
    }

    protected fun showCloseButtonForce() {
        stopShowCloseButton()
        blockCloseFlag = true
        unblockClose()
    }

    private fun handleSubscribed() {
        lifecycleScope.launchWhenResumed {
            finishOnDismiss()
        }
    }

    private fun finishOnDismiss() {
        if (isLiveActivity) {
            finish()
        }
    }

    private val isLiveActivity: Boolean
        get() = !isFinishing && !isDestroyed
}