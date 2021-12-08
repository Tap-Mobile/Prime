package com.prime.features.servers.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prime.features.servers.model.LoadableServersState
import com.prime.features.servers.model.LoadableServersState.EMPTY
import com.prime.features.servers.model.LoadableServersState.ERROR
import com.prime.features.servers.model.LoadableServersState.LOADING
import com.protonvpn.android.api.NetworkLoader
import com.protonvpn.android.components.LoaderUI
import com.protonvpn.android.ui.home.ServerListUpdater
import me.proton.core.network.domain.ApiResult

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class AppServersLoaderManager constructor(
    val serversLoader: AppServersLoader,
    val serverListUpdater: ServerListUpdater
) : NetworkLoader {
    override fun getNetworkFrameLayout(): LoaderUI = serversLoader

    fun startSchedule(lifecycle: Lifecycle) = serverListUpdater.startSchedule(lifecycle, this)
}

class AppServersLoader : LoaderUI {
    private val _stateObservable = MutableLiveData(EMPTY)
    override val state: LoadableServersState
        get() = _stateObservable.value!!
    val stateObservable: LiveData<LoadableServersState> get() = _stateObservable

    override fun switchToRetry(error: ApiResult.Error) {
        _stateObservable.postValue(ERROR)
    }

    override fun switchToEmpty() {
        _stateObservable.postValue(EMPTY)
    }

    override fun switchToLoading() {
        _stateObservable.postValue(LOADING)
    }

    override fun setRetryListener(listener: () -> Unit) = Unit
}