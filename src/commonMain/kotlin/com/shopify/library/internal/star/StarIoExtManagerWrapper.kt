package com.shopify.library.internal.star

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface responsible for wrapping the platform specific implementation of the StarIoExtManager.
 * iOS and Android both have their own platform specific implementations of this class, provided by the StarSdk
 * Normally, in a production app we would have both iOS and Android implementations of this
 * interface - but for the purposes of this sample project we only implement iOS.
 */
interface StarIoExtManagerWrapper {
    suspend fun print(releasePort: Boolean): Boolean
    fun connect(portName: String)
    fun disconnect()
    fun setStatusListener(listener: (String) -> Unit)
}