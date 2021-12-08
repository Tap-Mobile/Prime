package com

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.di.AppInjector
import com.getkeepsafe.relinker.ReLinker
import com.github.anrwatchdog.ANRWatchDog
import com.prime.data.AppDependenciesLauncher
import com.prime.features.servers.core.AppServersLoaderManager
import com.prime.utils.ProcessUtils
import com.protonvpn.android.components.NotificationHelper
import com.protonvpn.android.utils.PrimeLogger
import com.protonvpn.android.utils.ProtonPreferences
import com.protonvpn.android.utils.Storage
import com.vpn.prime.BuildConfig
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import net.danlew.android.joda.JodaTimeAndroid
import org.strongswan.android.logic.StrongSwanApplication
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeApp : DaggerApplication(), LifecycleObserver {

    companion object {
        @JvmStatic
        fun getAppContext(): Context = StrongSwanApplication.getContext()

        fun setAppContextForTest(context: Context) = StrongSwanApplication.setContext(context)
    }

    private val appInjector: AppInjector by lazy(NONE) { AppInjector.init(this) }

    @Inject lateinit var diLauncher: AppDependenciesLauncher
    @Inject lateinit var serversLoaderManager: AppServersLoaderManager

    override fun onCreate() {
        super.onCreate()
        onCreateAllProcesses()

        if (ProcessUtils.isMainProcess(this)) {
            onCreateMainProcess()
        } else {
            onCreateAuxProcess()
        }
    }

    private fun onCreateAllProcesses() {
        initializeDebug()
    }

    private fun onCreateMainProcess() {
        Timber.d("Process: onMainProcess")
        initStrongSwan()
        initPreferences()
        appInjector.appComponent.inject(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //disable dark until app support it
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        NotificationHelper.initNotificationChannel(this)
        JodaTimeAndroid.init(this)
        ANRWatchDog(15_000).start()

        registerActivityLifecycleCallbacks(diLauncher.activityTracker)

        PrimeLogger.log("App start")

        serversLoaderManager.startSchedule(ProcessLifecycleOwner.get().lifecycle)
    }

    private fun onCreateAuxProcess() {
        Timber.d("Process: onAuxProcess")
    }

    private fun initializeDebug() {
        if (BuildConfig.DEBUG) {
            //Stetho.initializeWithDefaults(this);
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? =
                    "(%s:%s)[%s]".format(
                        Locale.US,
                        element.fileName,
                        element.lineNumber,
                        element.methodName
                    )
            })
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        PrimeLogger.log("App in foreground " + BuildConfig.VERSION_NAME)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            PrimeLogger.log(
                "Battery optimization ignored: ${
                    pm.isIgnoringBatteryOptimizations(
                        packageName
                    )
                }"
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() = PrimeLogger.log("App in background")

    private fun initStrongSwan() {
        ReLinker.loadLibrary(this, "androidbridge")
        // Static blocks from StrongSwanApplication will execute here loading native library and initializing
        // certificate store.
        StrongSwanApplication.setContext(applicationContext)
        //init(baseContext)
    }

    //TODO R2 optimize speedup?
    private fun initPreferences() {
        Storage.setPreferences(
            ProtonPreferences(
                this,
                BuildConfig.PREF_SALT,
                BuildConfig.PREF_KEY,
                "Proton-Secured"
            )
        )
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appInjector.appComponent
}