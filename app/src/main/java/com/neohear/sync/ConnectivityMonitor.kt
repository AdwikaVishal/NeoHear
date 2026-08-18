package com.neohear.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ConnectivityState { ONLINE, OFFLINE, SYNCING }

/**
 * Monitors device connectivity and exposes a StateFlow. Uses ConnectivityManager callbacks
 * and does not poll. For tests, consider substituting a fake monitor.
 */
class ConnectivityMonitor private constructor(private val context: Context) {

    private val _state = MutableStateFlow(ConnectivityState.OFFLINE)
    val state: StateFlow<ConnectivityState> = _state

    private val conn by lazy { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _state.value = ConnectivityState.ONLINE
        }

        override fun onLost(network: Network) {
            _state.value = ConnectivityState.OFFLINE
        }
    }

    init {
        try {
            val req = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            conn.registerNetworkCallback(req, callback)
            // initial state
            val active = conn.activeNetwork
            if (active != null) {
                _state.value = ConnectivityState.ONLINE
            } else {
                _state.value = ConnectivityState.OFFLINE
            }
        } catch (e: Exception) {
            _state.value = ConnectivityState.OFFLINE
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ConnectivityMonitor? = null

        fun getInstance(context: Context): ConnectivityMonitor {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ConnectivityMonitor(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
