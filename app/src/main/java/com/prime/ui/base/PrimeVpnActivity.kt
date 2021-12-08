package com.prime.ui.base

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.IBinder
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.protonvpn.android.bus.EventBus
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.profiles.Profile
import com.protonvpn.android.utils.AndroidUtils.isTV
import com.protonvpn.android.utils.Log
import com.protonvpn.android.vpn.VpnConnectionManager
import com.vpn.prime.R
import org.strongswan.android.logic.CharonVpnService
import org.strongswan.android.logic.TrustedCertificateManager
import org.strongswan.android.logic.VpnStateService
import org.strongswan.android.logic.VpnStateService.LocalBinder
import java.io.File
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
abstract class PrimeVpnActivity : PrimeBaseActivity() {

    open val isRegisteredForEvents = true

    private var mService: VpnStateService? = null

    @Inject lateinit var userData: UserData
    @Inject protected lateinit var vpnConnectionManager: VpnConnectionManager

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = (service as LocalBinder).service
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkOrientation()
        if (isRegisteredForEvents) {
            EventBus.getInstance().register(this)
        }

        this.bindService(
            Intent(this, VpnStateService::class.java), mServiceConnection,
            BIND_AUTO_CREATE
        )
        Log.checkForLogTruncation(filesDir.toString() + File.separator + CharonVpnService.LOG_FILE)
        LoadCertificatesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegisteredForEvents) {
            EventBus.getInstance().unregister(this)
        }
        if (mService != null) {
            unbindService(mServiceConnection)
        }
    }

    private fun checkOrientation() {
        requestedOrientation =
            if (resources.getBoolean(R.bool.isTablet) || this.isTV()) ActivityInfo.SCREEN_ORIENTATION_FULL_USER
            else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun onConnect(profileToConnect: Profile) {
        val server = profileToConnect.server
        if (userData.hasAccessToServer(server) && server!!.online || server == null) {
            vpnConnectionManager.connect(
                this,
                profileToConnect,
                "mobile home screen (unspecified)"
            )
        } else {
            connectingToRestrictedServer(profileToConnect)
        }
    }

    private fun connectingToRestrictedServer(profileToConnect: Profile) {
        if (profileToConnect.server!!.online) {
            vpnConnectionManager.connect(
                this,
                profileToConnect,
                "mobile home screen (fallback to premium)"
            )
        } else {
            //TODO R2 maybe try auto connect to another server?
            MaterialDialog.Builder(this).theme(Theme.DARK)
                .title(R.string.restrictedMaintenanceTitle)
                .content(R.string.restrictedMaintenanceDescription)
                .negativeText(R.string.cancel)
                .show()
        }
    }

    /**
     * Class that loads the cached CA certificates.
     */
    @SuppressLint("StaticFieldLeak")
    private inner class LoadCertificatesTask : AsyncTask<Void, Void, TrustedCertificateManager>() {
        override fun onPreExecute() {
            setProgressBarIndeterminateVisibility(true)
        }

        override fun doInBackground(vararg params: Void): TrustedCertificateManager {
            return TrustedCertificateManager.getInstance().load()
        }

        override fun onPostExecute(result: TrustedCertificateManager) {
            setProgressBarIndeterminateVisibility(false)
        }
    }
}