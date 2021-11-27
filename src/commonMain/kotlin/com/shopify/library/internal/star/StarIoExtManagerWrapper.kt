package com.shopify.library.internal.star

import kotlinx.coroutines.flow.StateFlow

interface StarIoExtManagerWrapper {
    val printerStatus: StateFlow<String>

    suspend fun print(releasePort: Boolean): Boolean
    fun connect(portName: String)
    fun disconnect()
}