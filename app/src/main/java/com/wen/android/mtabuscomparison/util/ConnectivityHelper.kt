package com.wen.android.mtabuscomparison.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import timber.log.Timber

object ConnectivityHelper {
    private val TAG: String? = ConnectivityHelper::class.java.simpleName

    fun unregisterNetworkStatusCallback(context: Context, callback: NetworkCallback) {
        try {
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            manager.unregisterNetworkCallback(callback)
        } catch (e: Exception) {
            //todo report?
            Timber.e(e.localizedMessage)
        }
    }

    fun registerNetworkStatusCallback(context: Context, callback: NetworkCallback) {
        try {
            //if there are no Wi-Fi or mobile data presented when callback is registered
            //none of NetworkCallback methods are called
            callback.onNetworkUnavailable()

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                .build()
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            manager.registerNetworkCallback(request, callback)
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
        }

    }

    fun isNetworkEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork: Network? = manager.activeNetwork
            val caps: NetworkCapabilities? = manager.getNetworkCapabilities(activeNetwork)
            return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                manager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    abstract class NetworkCallback : ConnectivityManager.NetworkCallback() {

        abstract fun onNetworkAvailable()

        abstract fun onNetworkUnavailable()

        override fun onAvailable(network: Network) {
            onNetworkAvailable()
        }

        override fun onUnavailable() {
            onNetworkUnavailable()
        }

        override fun onLost(network: Network) {
            onNetworkUnavailable()
        }
    }
}
